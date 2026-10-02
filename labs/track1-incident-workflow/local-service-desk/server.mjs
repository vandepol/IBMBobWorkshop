#!/usr/bin/env node
// Local Service Desk — a stand-in for a ServiceNow developer instance.
//
// Implements the slice of the ServiceNow Table API that local-servicenow-mcp
// calls (incident, kb_knowledge, sys_user), and serves a small web UI so
// participants can read the incident Bob writes. No dependencies; state is
// kept in data.json next to this file so it survives restarts.
//
//   node local-service-desk/server.mjs            # http://localhost:8099
//   node local-service-desk/server.mjs --reset    # start with an empty desk

import http from "node:http";
import fs from "node:fs";
import path from "node:path";
import crypto from "node:crypto";
import { fileURLToPath } from "node:url";

const PORT = Number(process.env.SERVICE_DESK_PORT || 8099);
const DIR = path.dirname(fileURLToPath(import.meta.url));
const DATA_FILE = path.join(DIR, "data.json");

const STATES = { 1: "New", 2: "In Progress", 3: "On Hold", 6: "Resolved", 7: "Closed", 8: "Canceled" };
const LEVELS = { 1: "1 - High", 2: "2 - Medium", 3: "3 - Low" };

const now = () => new Date().toISOString().replace("T", " ").slice(0, 19);
const sysId = () => crypto.randomBytes(16).toString("hex");

function seed() {
  return {
    nextNumber: 10001,
    incident: [],
    sys_user: [
      { sys_id: sysId(), user_name: "admin", name: "System Administrator", email: "admin@example.com", active: "true" },
      { sys_id: sysId(), user_name: "bob", name: "IBM Bob (automation)", email: "bob@example.com", active: "true" },
    ],
    kb_knowledge: [
      {
        sys_id: sysId(), number: "KB0010001", short_description: "Bank app: slow pages under heavy load",
        text: "Symptoms: page loads of 3-5 seconds, backend CPU above 90%, requests per second far above the single instance's capacity of ~50. Check /api/admin/metrics on the backend and run the Ansible health-check playbook. Standard fix: scale the backend horizontally (increase replicas in Terraform) and place a load balancer in front. Always run terraform plan before apply, then re-verify metrics before resolving.",
      },
      {
        sys_id: sysId(), number: "KB0010002", short_description: "Incident handling standard: work notes and closure",
        text: "Record every diagnostic and remediation step as a work note as it happens. Resolve with close_code 'Solution provided' (or the closest match) and close_notes describing root cause, fix and verification evidence.",
      },
    ],
  };
}

function load() {
  if (process.argv.includes("--reset") && !load.resetDone) {
    load.resetDone = true;
    save(seed());
  }
  try { return JSON.parse(fs.readFileSync(DATA_FILE, "utf8")); } catch { const d = seed(); save(d); return d; }
}
function save(d) { fs.writeFileSync(DATA_FILE, JSON.stringify(d, null, 2)); }

// ServiceNow encoded queries: "a=b^c=d", "fieldLIKEtext", "textLIKEtext".
function matches(rec, query) {
  if (!query) return true;
  return query.split("^").every((clause) => {
    let m = clause.match(/^([\w.]+)LIKE(.*)$/);
    if (m) {
      const needle = m[2].toLowerCase();
      const hay = m[1] === "text" ? Object.values(rec).join(" ") : String(rec[m[1]] ?? "");
      return needle.split(/\s+/).some((w) => hay.toLowerCase().includes(w));
    }
    m = clause.match(/^([\w.]+)=(.*)$/);
    if (!m) return true;
    const [, field, value] = m;
    if (field === "assigned_to.user_name") return rec.assigned_to === value;
    if (field === "active") return String(Number(rec.state) < 6) === value;
    return String(rec[field] ?? "") === value;
  });
}

// Shape a record the way the Table API returns it: journal fields are write-only.
function present(rec) {
  if (!rec.journal) return rec;
  const { journal, ...rest } = rec;
  return { ...rest, active: String(Number(rec.state) < 6), work_notes: "" };
}

function send(res, status, body, type = "application/json") {
  res.writeHead(status, { "Content-Type": type });
  res.end(type === "application/json" ? JSON.stringify(body) : body);
}

function readBody(req) {
  return new Promise((resolve) => {
    let raw = "";
    req.on("data", (c) => (raw += c));
    req.on("end", () => { try { resolve(raw ? JSON.parse(raw) : {}); } catch { resolve({}); } });
  });
}

async function api(req, res, table, id, params) {
  const db = load();
  if (!db[table]) return send(res, 400, { error: { message: "Invalid table", detail: table } });
  const rows = db[table];

  if (req.method === "GET") {
    if (id) {
      const rec = rows.find((r) => r.sys_id === id);
      return rec ? send(res, 200, { result: present(rec) }) : send(res, 404, { error: { message: "No Record found" } });
    }
    const limit = Number(params.get("sysparm_limit") || 10);
    const found = rows.filter((r) => matches(r, params.get("sysparm_query"))).reverse().slice(0, limit);
    return send(res, 200, { result: found.map(present) });
  }

  if (req.method === "POST" && table === "incident") {
    const body = await readBody(req);
    const rec = {
      sys_id: sysId(),
      number: `INC00${db.nextNumber++}`,
      short_description: body.short_description || "",
      description: body.description || "",
      urgency: String(body.urgency || 3),
      impact: String(body.impact || 3),
      priority: String(Math.min(5, Number(body.urgency || 3) + Number(body.impact || 3) - 1)),
      state: "1",
      opened_at: now(),
      opened_by: "admin",
      assigned_to: "",
      close_code: "",
      close_notes: "",
      journal: [{ at: now(), kind: "opened", text: body.description || body.short_description || "" }],
    };
    rows.push(rec);
    save(db);
    return send(res, 201, { result: present(rec) });
  }

  if ((req.method === "PATCH" || req.method === "PUT") && table === "incident" && id) {
    const rec = rows.find((r) => r.sys_id === id);
    if (!rec) return send(res, 404, { error: { message: "No Record found" } });
    const body = await readBody(req);
    if (body.state && ["6", "7"].includes(String(body.state)) && !(body.close_code || rec.close_code)) {
      return send(res, 403, { error: { message: "Operation Failed", detail: "Data Policy Exception: Resolution code and Resolution notes are mandatory when resolving" } });
    }
    if (body.work_notes) rec.journal.push({ at: now(), kind: "work note", text: body.work_notes });
    if (body.state && String(body.state) !== rec.state) {
      rec.journal.push({ at: now(), kind: "state", text: `${STATES[rec.state]} → ${STATES[body.state] || body.state}` });
      rec.state = String(body.state);
      if (["6", "7"].includes(rec.state)) rec.resolved_at = now();
    }
    for (const k of ["assigned_to", "close_code", "close_notes", "urgency", "impact", "short_description"]) {
      if (body[k] !== undefined) rec[k] = String(body[k]);
    }
    if (body.close_notes) rec.journal.push({ at: now(), kind: "resolution", text: `${rec.close_code}: ${body.close_notes}` });
    rec.sys_updated_on = now();
    save(db);
    return send(res, 200, { result: present(rec) });
  }

  send(res, 405, { error: { message: "Method not supported in the local service desk" } });
}

// ---------- Web UI ----------

const esc = (s) => String(s ?? "").replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" }[c]));

const CSS = `
:root{--bg:#f4f6f8;--panel:#fff;--ink:#1d2329;--muted:#5d6b78;--line:#dde3e8;--accent:#0f62fe;--ok:#198038;--warn:#b28600;--bad:#da1e28}
@media (prefers-color-scheme:dark){:root{--bg:#14181c;--panel:#1d2329;--ink:#e8edf1;--muted:#97a4b0;--line:#2e3740;--accent:#78a9ff;--ok:#42be65;--warn:#f1c21b;--bad:#ff8389}}
*{box-sizing:border-box}body{margin:0;background:var(--bg);color:var(--ink);font:15px/1.5 -apple-system,BlinkMacSystemFont,"Segoe UI",sans-serif}
header{background:var(--panel);border-bottom:1px solid var(--line);padding:14px 24px;display:flex;align-items:baseline;gap:14px}
header b{font-size:17px}header span{color:var(--muted);font-size:13px}
main{max-width:1100px;margin:0 auto;padding:24px 16px}
table{width:100%;border-collapse:collapse;background:var(--panel);border:1px solid var(--line);border-radius:8px;overflow:hidden}
th,td{text-align:left;padding:10px 12px;border-bottom:1px solid var(--line);vertical-align:top}th{font-size:12px;text-transform:uppercase;letter-spacing:.04em;color:var(--muted)}
a{color:var(--accent);text-decoration:none}a:hover{text-decoration:underline}
.pill{display:inline-block;padding:1px 9px;border-radius:99px;font-size:12px;font-weight:600;border:1px solid currentColor}
.s1,.s2,.s3{color:var(--warn)}.s6,.s7{color:var(--ok)}.s8{color:var(--muted)}
.card{background:var(--panel);border:1px solid var(--line);border-radius:8px;padding:18px 20px;margin-bottom:18px}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(170px,1fr));gap:10px 24px}.grid div small{display:block;color:var(--muted);font-size:12px}
h1{font-size:22px;margin:0 0 6px}h2{font-size:15px;margin:0 0 12px;color:var(--muted);text-transform:uppercase;letter-spacing:.04em}
.tl{list-style:none;margin:0;padding:0}.tl li{border-left:3px solid var(--line);padding:4px 0 14px 16px;position:relative}
.tl li:before{content:"";position:absolute;left:-7px;top:9px;width:11px;height:11px;border-radius:50%;background:var(--accent)}
.tl li.resolution:before,.tl li.state:before{background:var(--ok)}
.tl small{color:var(--muted)}.tl pre{white-space:pre-wrap;font:inherit;margin:4px 0 0}
.empty{color:var(--muted);padding:40px;text-align:center;background:var(--panel);border:1px dashed var(--line);border-radius:8px}
@media (max-width:640px){th:nth-child(4),td:nth-child(4){display:none}}
`;

function page(title, body) {
  return `<!doctype html><html><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>${esc(title)}</title><meta http-equiv="refresh" content="4"><style>${CSS}</style></head>
<body><header><b>Local Service Desk</b><span>workshop stand-in for ServiceNow · refreshes every 4s</span></header><main>${body}</main></body></html>`;
}

function listPage() {
  const rows = load().incident.slice().reverse();
  if (!rows.length) return page("Incidents", `<div class="empty">No incidents yet. They appear here the moment Bob opens one.</div>`);
  return page("Incidents", `<table><tr><th>Number</th><th>Short description</th><th>State</th><th>Opened</th><th>Priority</th></tr>${rows
    .map((r) => `<tr><td><a href="/incident/${esc(r.number)}">${esc(r.number)}</a></td><td>${esc(r.short_description)}</td>
<td><span class="pill s${esc(r.state)}">${esc(STATES[r.state] || r.state)}</span></td><td>${esc(r.opened_at)}</td><td>${esc(r.priority)}</td></tr>`)
    .join("")}</table>`);
}

function detailPage(number) {
  const r = load().incident.find((i) => i.number === number);
  if (!r) return null;
  const field = (k, v) => `<div><small>${k}</small>${esc(v) || "—"}</div>`;
  return page(`${r.number}`, `<p><a href="/">← All incidents</a></p>
<div class="card"><h1>${esc(r.number)} · ${esc(r.short_description)}</h1>
<div class="grid">${field("State", STATES[r.state])}${field("Urgency", LEVELS[r.urgency])}${field("Impact", LEVELS[r.impact])}
${field("Opened", r.opened_at)}${field("Resolved", r.resolved_at)}${field("Close code", r.close_code)}</div></div>
${r.description ? `<div class="card"><h2>Description</h2><pre style="white-space:pre-wrap;font:inherit;margin:0">${esc(r.description)}</pre></div>` : ""}
<div class="card"><h2>Activity</h2><ul class="tl">${r.journal
    .map((j) => `<li class="${j.kind === "resolution" || j.kind === "state" ? j.kind : ""}"><small>${esc(j.at)} · ${esc(j.kind)}</small><pre>${esc(j.text)}</pre></li>`)
    .join("")}</ul></div>`);
}

http
  .createServer(async (req, res) => {
    const url = new URL(req.url, `http://localhost:${PORT}`);
    const m = url.pathname.match(/^\/api\/now\/table\/(\w+)(?:\/(\w+))?$/);
    try {
      if (m) return await api(req, res, m[1], m[2], url.searchParams);
      if (url.pathname === "/") return send(res, 200, listPage(), "text/html; charset=utf-8");
      const d = url.pathname.match(/^\/incident\/(\w+)$/);
      if (d) {
        const html = detailPage(d[1]);
        return html ? send(res, 200, html, "text/html; charset=utf-8") : send(res, 404, page("Not found", `<div class="empty">No incident ${esc(d[1])}</div>`), "text/html; charset=utf-8");
      }
      if (url.pathname === "/health") return send(res, 200, { ok: true });
      send(res, 404, { error: { message: "Not found" } });
    } catch (e) {
      send(res, 500, { error: { message: e.message } });
    }
  })
  .listen(PORT, () => {
    load();
    console.log(`Local Service Desk on http://localhost:${PORT}`);
  });

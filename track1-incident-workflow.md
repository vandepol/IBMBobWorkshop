# Track 1 — IBM Bob, standard
## Lab: an AI partner that runs your incident, not just your editor

**Time:** 60 minutes
**Environment:** pre-baked sandbox VM — nothing to install. Or your own laptop — see *Running on your own laptop* below.
**Bob tier:** standard (no premium entitlement needed)
**Tested on:** IBM Bob 2.2.1, macOS with Podman

---

## What you are about to do

A bank application is running happily on one backend instance. Traffic surges. Pages that took under a second start taking three to five. You hand that problem to Bob in a single sentence — and watch it open an incident, diagnose the cause with Ansible, rewrite the Terraform to scale out, verify the fix, and close the ticket with documentation.

The point of this hour is not that AI can write code. It is that a properly configured agent can drive the tools you already run your estate with.

**By the end you will have seen:**
- A **custom mode** — how an organisation encodes its own incident process into Bob
- **MCP servers** — how Bob is given real tools instead of guesses
- An agent orchestrating **ticketing, Ansible and Terraform** in one continuous piece of work
- Where it needed you, and where it did not

> **About the ticketing system.** Bob talks to a ServiceNow-compatible API through the ServiceNow MCP server, exactly as it would in production. For the workshop that API is served by the **Local Service Desk** on `http://localhost:8099` — no ServiceNow instance to wake, no shared credentials, and every participant gets their own clean desk. Pointing the same MCP server at a real ServiceNow instance is a one-line change in `.bob/mcp.json`.

---

## Running on your own laptop

Do this **before** the session; it needs the network and takes 5–10 minutes. On the sandbox VM it has already been done.

You need Docker Desktop, Colima or Podman (running), Terraform 1.x, Ansible core 2.x, Node 20+ and IBM Bob.

```bash
git clone https://github.com/vandepol/IBMBobWorkshop
cd IBMBobWorkshop/labs/track1-incident-workflow
./setup-local.sh
```

Then open `IBMBobWorkshop/labs/track1-incident-workflow` in Bob. Every command in this lab runs from that folder. If you move the folder, run `./setup-local.sh` again.

---

## 0:00–0:05 · Smoke test

Everything is pre-installed. This is purely to catch a bad image while there is still time to reseat you.

Open the terminal in Bob (**Terminal → New Terminal**) and run:

```bash
docker ps            # a table, empty or not — not an error
terraform version    # Terraform v1.x
ansible --version    # ansible [core 2.x]
node --version       # v20 or higher
```

Then, in Bob: click the **⚙ gear** at the top of the Bob chat panel to open **Bob Settings**, then choose **MCP** in the left-hand list. Wait a few seconds for the table to load. You must see three rows — **servicenow**, **terraform**, **ansible** — each with Status **Connected**. (Any other rows, such as `box`, do not matter.)

> 🚩 **If any of the three says anything other than Connected, Bob has no tools and the lab cannot run.** Raise your hand immediately — do not try to fix it yourself. It is almost always a path in `.bob/mcp.json` that does not match this machine — on your own laptop, re-run `./setup-local.sh` and restart Bob.

### ✋ CHECKPOINT 1 — *everyone has three Connected MCP servers*

Do not start the lab until the room is green. A participant who starts without working MCP servers will get twenty minutes in before it becomes obvious.

---

## 0:05–0:13 · Deploy the application and see it healthy

You need to know what "good" looks like before you break it.

```bash
./demo-scripts/bank-app/deploy-initial.sh
```

Terraform provisions a Docker network, a PostgreSQL database, **one** backend API instance and an Nginx frontend, and the script starts the Local Service Desk. Expect:

```
Application Status:
  • Frontend:  http://localhost
  • Backend:   http://localhost:5001
  • Database:  localhost:5437
  • Service Desk (incidents): http://localhost:8099

Infrastructure:
  • Backend replicas: 1 (adequate for low traffic)
```

Open **http://localhost** (in your browser, or in Bob with **⇧⌘P → Simple Browser: Show**). Sign in with **demo / demo123** and use it like a customer would: open a **Statement**, start a **Deposit**, start a **Withdraw**.

**Notice the speed.** Pages come back in well under a second. You are establishing a baseline you will feel the loss of in about eight minutes.

> 💡 While this deploys, open `bank-app/terraform/main.tf` and `bank-app/terraform/terraform.tfvars` side by side. `terraform.tfvars` asks for `backend_replicas = 1` — but search `main.tf` and you will find no such variable: there is exactly one hard-coded `docker_container "backend"` and nothing in front of it. That gap is the whole story of this lab. Keep it in mind; Bob is about to find it on its own.

---

## 0:13–0:18 · Cause a production incident

```bash
./demo-scripts/bank-app/setup-flow.sh
```

This simulates a traffic surge — the payday spike, the marketing campaign, the post that went viral. CPU to 95%, memory to 88%, 450 requests/second against a backend sized for 50.

**Go back to http://localhost and use the app again.**

Do this properly — actually click through it. Three to five seconds per page is a number on a slide; waiting three to five seconds for your own account balance is a feeling, and it is the feeling that makes the next twenty minutes land.

---

## 0:18–0:28 · Read the mode before you use it

This is the part people skip and it is the most important ten minutes in the lab. Everything Bob is about to do correctly, it does correctly *because of what is in this mode*.

1. In the Bob chat panel, click the **mode selector** (bottom of the panel; it reads **Agent** by default)
2. Click the **⚙ gear** in the header of the **Modes** list
3. In the Modes settings page that opens, click **🎫 SDLC Incident Manager**

Read the mode definition. Then open the instruction files on disk (**⌘P** and type the file name):

```
.bob/rules-sdlc-incident-manager/
├── 1_workflow.xml              ← the incident process itself
├── 2_best_practices.xml
├── 3_examples.xml
├── 4_quick_reference.xml
└── 5_session_incident_tracking.xml
```

Open `1_workflow.xml` and skim it. Then open `4_quick_reference.xml` and look at its structure — an incident workflow checklist, a ServiceNow reference, an Ansible reference, communication templates, success criteria.

**Ask yourself the question this lab is really about:** this is one team's incident process, written down in a form an agent can follow. What would your organisation's look like? Which of your runbooks is already almost this?

> 💡 The mode is a file in a repo. It gets code-reviewed, versioned and rolled back like anything else. That is the governance answer to "how do we control what the AI does" — you control it the same way you control everything else.

### ✋ CHECKPOINT 2 — *everyone has `1_workflow.xml` open and has seen the mode definition*

---

## 0:28–0:48 · Hand the incident to Bob

Start a fresh chat: click **+** (New Task) at the top of the Bob panel. Then click the mode selector and choose **🎫 SDLC Incident Manager**. Confirm the selector now reads **🎫 SDLC Incident Manager** before continuing.

Paste exactly this, and nothing else, and press Enter:

```
Users are reporting severe performance issues. Application is very slow, taking 3-5 seconds to load pages.
```

That is all the information Bob gets. It is roughly what you would get from a service desk at 2am.

### What to watch for — and approve

Bob asks permission before every action. Each request shows the tool, its arguments, and three buttons: **Approve once**, **Reject**, and **Approve for task**. **Use Approve once, and read each request before you click it.** "Approve for task" stops Bob asking — fine when you are short of time, but it defeats the point of the exercise. The approval gate is a feature you will want to talk about with your security people.

What Bob did in the tested run, in order (about 17 approvals, ~10 minutes of Bob time):

| Phase | What Bob does | Tool |
|---|---|---|
| **Record** | Opens an incident with a description, urgency and impact | ServiceNow MCP |
| **Diagnose** | Queries the metrics endpoint (`curl …/api/admin/metrics`) | command |
| | Runs the `health-check.yml` playbook against the inventory | Ansible MCP |
| | Writes its diagnosis to the incident and moves it to In Progress | ServiceNow MCP |
| **Pause** | Presents the plan (scale ×3 + Nginx load balancer) and **asks whether to proceed** | — |
| **Resolve** | Edits `frontend/nginx.conf`, `terraform.tfvars`, adds `nginx-lb.conf`, rewrites `main.tf` | file edits |
| | Runs `init`, then `plan`, then `apply` | Terraform MCP |
| **Verify** | Re-runs the health check, reads the load balancer logs, re-checks metrics | Ansible MCP / command |
| | Records the verification evidence on the incident | ServiceNow MCP |
| **Close** | Resolves the incident with a close code and close notes | ServiceNow MCP |

> ⏸️ **Bob will stop and ask "Shall I proceed with applying this remediation?"** before it changes any infrastructure. It is not stuck — it is waiting for you. Read the plan, then type `Yes, proceed` and press Enter. This is the mode doing its job: a human signs off before production changes.

### Three things worth noticing while it runs

1. **It ran `plan` before `apply`.** Nobody told it to in the prompt. That is in the mode.
2. **It verified before closing.** It did not declare victory on the basis of having made a change — it went back and measured.
3. **The ticket is being written as it goes,** not reconstructed afterwards. Open **http://localhost:8099** in a second tab and watch the incident's activity timeline fill in live.

> ℹ️ **How you know Bob is finished:** the chat shows a summary (files changed, before/after metrics) and the input box reads *"Follow up or start new task"*. There is no separate "task completed" banner.

> 🩹 **If Bob stalls or takes a wrong turn:** say so in chat, plainly — *"the health check hasn't returned, what's the current state?"* Recovering a stuck agent in conversation is a legitimate part of the demo, not a failure of it. If it is properly wedged, start a new task (**+**) and re-paste the incident report; the infrastructure state persists.

> ⏱️ **Running short on time?** Click **Approve for task** on the next request and let Bob finish. Seeing the arc complete matters more than scrutinising every step.

### ✋ CHECKPOINT 3 — *Bob has applied the Terraform change and is verifying*

---

## 0:48–0:56 · Verify the fix yourself

Do not take Bob's word for it.

**In the application:** go back to **http://localhost** and click around. Sub-second again.

**In the infrastructure:**

```bash
docker ps
```

You should now see `bank-app-dev-backend-1`, `-2` and `-3` and a `bank-app-dev-load-balancer` where there was one backend.

**In the service desk:** open **http://localhost:8099**, click your incident (e.g. `INC0010001`) and read the activity timeline end to end — opened, diagnosis, In Progress, remediation, verification, Resolved with close code *Solution provided*.

Read those work notes as an auditor would. Ask whether this is better or worse than what a human writes at 3am under pressure.

---

## 0:56–1:00 · Debrief

Take two minutes before your track lead pulls the room together:

- What did Bob do that you did not expect?
- Where did it need you? Where should it have needed you and didn't?
- Which of your own runbooks is closest to being a mode?
- What would have to be true — controls, audit, entitlements — for this to run against your real environment?

Bring one surprise and one criticism to the regroup. The criticism is the more useful of the two.

---

# Optional extras

Take these home. Nothing below is needed to complete the lab.

## A · Build your own mode *(~30 min, the highest-value extra)*

You will create a **Code Reviewer** mode from scratch, then use Bob's **Mode Writer** mode to flesh it out — Bob writing the configuration for Bob.

```bash
mkdir -p .bob/rules-code-reviewer
```

Mode definition:

```yaml
slug: code-reviewer
name: 🔍 Code Reviewer
description: Reviews code changes for quality, security, and best practices
role definition: You are a code reviewer helping to ensure code quality, security, and maintainability. Focus on security, code quality, performance, test coverage, and documentation
available tools: read files
```

Test it with a basic `1_workflow.xml`, switch to the mode and ask:

```
Please review this codebase
```

Then switch to **✍️ Mode Writer** and ask:

```
Please flesh out the `code-reviewer` mode, it is currently a basic draft implementation. Include:
1. Workflow
2. Best Practices
3. Tool Usage
4. Examples
```

Switch back to Code Reviewer, run the same review prompt, and compare. The difference between the two runs is the argument for investing in modes.

## B · Break it differently *(~20 min)*

The lab's incident is always under-provisioning, so Bob always scales out. Introduce a different fault — stop the database container, exhaust a connection pool, add latency to a dependency — and run the same incident report. Watch whether the diagnosis actually follows the evidence or pattern-matches to the fix it used last time. This is the sharpest test of the mode.

## C · Tear it down

```bash
./demo-scripts/bank-app/shutdown-flow.sh
```

Clears metrics and delays, destroys the Terraform infrastructure, removes containers and networks, stops the Local Service Desk, resets configuration. Not needed on workshop day — the sandbox is disposable — but useful if you keep working in it.

---

# Appendix A · The Local Service Desk

- **URL:** http://localhost:8099 — incident list; click a number for the detail and activity timeline. Pages refresh every 4 seconds.
- **API:** `http://localhost:8099/api/now/table/{incident,kb_knowledge,sys_user}` — the ServiceNow Table API subset the MCP server uses. It enforces ServiceNow's rule that resolving needs a close code and close notes.
- **Data:** `local-service-desk/data.json`. Reset with `node local-service-desk/server.mjs --reset` (or rerun `deploy-initial.sh`, which starts it fresh).
- **Using a real ServiceNow instance instead:** in `.bob/mcp.json`, replace `"SERVICENOW_BASE_URL"` in the `servicenow` server's `env` with `SERVICENOW_INSTANCE`, `SERVICENOW_USERNAME` and `SERVICENOW_PASSWORD` (from a secret store, not the repo), then restart Bob.

---

# Appendix B · Troubleshooting

| Symptom | What to do |
|---|---|
| A server in Bob Settings → MCP is not **Connected** | Raise your hand. A path in `.bob/mcp.json` does not match this machine |
| Ticket calls fail | Check http://localhost:8099 loads. If not: `node local-service-desk/server.mjs &` from the repo root |
| `http://localhost` will not load after deploy | `docker ps` to confirm containers are up; give it 30 seconds and retry |
| Bob appears frozen mid-task | Check whether it asked you a question at the bottom of the chat. Otherwise ask in chat what the current state is. Long Terraform applies genuinely take time |
| Bob proposed something that looks wrong | Click **Reject** and ask why it proposed it. This is a good moment, not a bad one — bring it to the debrief |

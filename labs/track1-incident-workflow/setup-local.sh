#!/bin/bash
# One-time setup for running the Track 1 lab on your own machine (the sandbox VM is already set up).
#   1. checks Docker, Terraform, Ansible and Node 20+
#   2. builds the three MCP servers
#   3. points .bob/mcp.json at this folder (Bob needs absolute paths) and at your Docker socket
#   4. downloads the Terraform providers and pre-pulls the base images so the lab doesn't wait on the network
# Safe to re-run. Usage: ./setup-local.sh
set -uo pipefail

LAB_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$LAB_DIR"
source demo-scripts/docker-host.sh

ok()   { echo "  ✓ $1"; }
fail() { echo "  ✗ $1"; FAILED=1; }
FAILED=0

echo "== 1/4 Prerequisites"
if ! command -v docker > /dev/null 2>&1; then
    fail "docker CLI not found — install Docker Desktop, Colima or Podman"
elif ! docker ps > /dev/null 2>&1; then
    fail "Docker is installed but not running — start Docker Desktop / 'colima start' / 'podman machine start'"
else
    ok "Docker running (${DOCKER_HOST:-unix:///var/run/docker.sock})"
fi
command -v terraform > /dev/null 2>&1 && ok "$(terraform version | head -1)" || fail "terraform not found — https://developer.hashicorp.com/terraform/install"
command -v ansible > /dev/null 2>&1 && ok "$(ansible --version | head -1)" || fail "ansible not found — 'brew install ansible' or 'pipx install ansible-core'"
if command -v node > /dev/null 2>&1 && [ "$(node -p 'process.versions.node.split(".")[0]')" -ge 20 ]; then
    ok "Node $(node --version)"
else
    fail "Node 20 or higher not found — https://nodejs.org"
fi
if [ $FAILED -ne 0 ]; then
    echo; echo "Fix the items above and re-run ./setup-local.sh"; exit 1
fi

echo "== 2/4 Building MCP servers"
./build-mcp-servers.sh > /tmp/track1-mcp-build.log 2>&1 && ok "servicenow, terraform, ansible built" || {
    echo "  ✗ build failed — see /tmp/track1-mcp-build.log"; exit 1; }

echo "== 3/4 Writing .bob/mcp.json paths"
LAB_DIR="$LAB_DIR" DOCKER_HOST="${DOCKER_HOST:-}" node - <<'JS'
const fs = require("fs");
const file = ".bob/mcp.json";
const cfg = JSON.parse(fs.readFileSync(file, "utf8"));
const dirs = { servicenow: "local-servicenow-mcp", terraform: "terraform-mcp-server", ansible: "ansible-mcp-server" };
const out = { servicenow: "dist", terraform: "build", ansible: "build" };
for (const [name, dir] of Object.entries(dirs)) {
  const s = cfg.mcpServers[name];
  s.cwd = `${process.env.LAB_DIR}/${dir}`;
  s.args = [`${s.cwd}/${out[name]}/index.js`];
  // Bob is launched from the Dock, so it won't see a DOCKER_HOST exported in your shell.
  if (name !== "servicenow") {
    s.env = s.env || {};
    if (process.env.DOCKER_HOST) s.env.DOCKER_HOST = process.env.DOCKER_HOST;
    else delete s.env.DOCKER_HOST;
    if (!Object.keys(s.env).length) delete s.env;
  }
}
fs.writeFileSync(file, JSON.stringify(cfg, null, 2) + "\n");
JS
ok "MCP servers point at $LAB_DIR"

echo "== 4/4 Pre-fetching Terraform providers and images"
(cd bank-app/terraform && terraform init -input=false > /dev/null) && ok "terraform init (bank-app)" || fail "terraform init failed"
for img in postgres:16-alpine node:20-alpine nginx:alpine; do
    docker pull -q "$img" > /dev/null 2>&1 && ok "pulled $img" || echo "  ! could not pull $img (the deploy will retry)"
done

echo
echo "Done. Next:"
echo "  1. Open this folder in IBM Bob: $LAB_DIR"
echo "  2. Bob ⚙ → MCP: servicenow, terraform, ansible should all show Connected (restart Bob if not)"
echo "  3. Follow track1-incident-workflow.md (repo root) from the smoke test"

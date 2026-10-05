# IBM Bob Workshop

Hands-on labs for IBM Bob, each about 60 minutes. Participants can run them on a prepared sandbox VM or on their own laptop.

| Lab | What it shows | Bob tier |
|---|---|---|
| [Track 1 — Incident workflow](track1-incident-workflow.md) | A custom mode and MCP servers let Bob run an incident end to end: open the ticket, diagnose with Ansible, scale out with Terraform, verify, close | Standard |
| [Track 2 — Java 8 → 21](track2-java-upgrade.md) | The Java Modernization workflow upgrades a real application and gets it to a green build | Premium Package for Java |
| [Track 3 — Mainframe (Z)](track3-z-bank-of-z.md) | Understanding a CICS / IMS / DB2 COBOL bank you've never seen, using Bank of Z | Premium Package for Z |

## Layout

Each track is one guide at the top level. Tracks 1 and 2 also have a folder under `labs/` with the same name; that folder is what you open in Bob. Track 3 uses the public [Bank of Z](https://github.com/IBM/Bank-of-Z) repository, cloned as part of its pre-work.

```
track1-incident-workflow.md      labs/track1-incident-workflow/   bank app, Terraform, Ansible, MCP servers, Local Service Desk
track2-java-upgrade.md           labs/track2-java-upgrade/        Java 8 pharmacy app (Struts, Maven)
track3-z-bank-of-z.md            (clone IBM/Bank-of-Z)            Bank of Z: CICS / IMS / DB2 COBOL
```

## Setup on your own laptop

Clone once:

```bash
git clone https://github.com/vandepol/IBMBobWorkshop
```

Then follow the **Running on your own laptop** section at the top of your track's guide. For Track 1 it comes down to:

```bash
cd IBMBobWorkshop/labs/track1-incident-workflow
./setup-local.sh
```

`setup-local.sh` checks for Docker (Desktop, Colima or Podman), Terraform, Ansible and Node 20+. It then builds the MCP servers, writes this folder's absolute paths into `.bob/mcp.json` (Bob needs absolute paths), sets `DOCKER_HOST` for Colima or Podman, runs `terraform init` and pre-pulls the images. Run it again if you move the folder.

Track 1 needs no ServiceNow instance or credentials. A ServiceNow-compatible Local Service Desk runs on `http://localhost:8099`, and pointing the lab at a real instance is a config change described in the guide.

## Setup on a sandbox VM

Clone the repo to the same path on every VM (for example `~/workshop/IBMBobWorkshop`). Then do each track's laptop setup once on the image, and check before you snapshot it:

- **Track 1:** Bob ⚙ → MCP shows **servicenow**, **terraform** and **ansible** as **Connected** with `labs/track1-incident-workflow` open.
- **Track 2 Java:** Bob's ▶ workflow list shows **Java Modernization** with `labs/track2-java-upgrade` open.
- **Track 3 Z:** **Z Code** and **Z Architect** appear in the mode selector.

## Resetting between runs

Bob edits files during the labs. Restore a lab folder with `git checkout -- <folder> && git clean -fd <folder>`. For Track 1, run `./demo-scripts/bank-app/shutdown-flow.sh` first.

## Sample credentials

The sample applications ship with demo logins (for example `demo` / `demo123` for the Track 1 bank app) and seeded password hashes. These are test fixtures for local containers, not real credentials.

## Sources

- Track 1 is adapted from the `bob-incidents-workflow` lab. Changes: the Local Service Desk replaces a hosted ServiceNow instance; no credentials are in the repo; script paths are fixed; and the Docker socket is detected automatically.
- Track 2 Java is the lab 2 starting snapshot from the Bob Java modernization labs.
- Track 3 Z uses [IBM/Bank-of-Z](https://github.com/IBM/Bank-of-Z).

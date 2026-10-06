# IBM Bob Workshop

Hands-on labs for IBM Bob, each about 60–90 minutes including setup. Participants run them on the IBM TechZone Red Hat VM (in the browser) or on their own laptop. Each guide starts with the setup steps, so a fresh VM is all anyone needs.

| Lab | What it shows | Bob tier |
|---|---|---|
| [Track 1 — Incident workflow](track1-incident-workflow.md) | A custom mode and MCP servers let Bob run an incident end to end: open the ticket, diagnose with Ansible, scale out with Terraform, verify, close | Standard |
| [Track 2 — Java 8 → 21](track2-java-upgrade.md) | The Java Modernization workflow upgrades a real application to Java 21 and Jakarta EE 10, then Bob gets it running on Liberty with Struts 7 | Premium Package for Java |
| [Bonus 2b — WebSphere → Liberty](track2b-liberty-replatforming.md) | The Liberty Modernization workflow takes an AMA migration plan and replatforms the pharmacy from WebSphere traditional to Liberty — recipes, two reasoned fixes, a local deployment | Premium Package for Java |
| [Track 3 — Mainframe (Z)](track3-z-bank-of-z.md) | Understanding a CICS / IMS / DB2 COBOL bank you've never seen, using Bank of Z | Premium Package for Z |

## Layout

Each track is one guide at the top level. Tracks 1 and 2 also have a folder under `labs/` with the same name; that folder is what you open in Bob. Track 3 uses the public [Bank of Z](https://github.com/IBM/Bank-of-Z) repository, cloned as part of its pre-work.

```
track1-incident-workflow.md      labs/track1-incident-workflow/   bank app, Terraform, Ansible, MCP servers, Local Service Desk
track2-java-upgrade.md           labs/track2-java-upgrade/        Java 8 pharmacy app (Struts, Maven)
track2b-liberty-replatforming.md labs/track2b-liberty-replatforming/  Same pharmacy app, packaged for WebSphere traditional
                                 labs/track2b-migration-plan/     The AMA migration plan (.zip) the workflow takes as input
track3-z-bank-of-z.md            (clone IBM/Bank-of-Z)            Bank of Z: CICS / IMS / DB2 COBOL
```

## Setup

Every participant runs their track's setup at the start of the lab — it's the first section of each guide, about 15 minutes, and covers both the TechZone VM and a Mac laptop:

- **Track 1:** install Node, Ansible, Terraform and the Podman Docker shim with `dnf`, allow port 80 for rootless Podman, clone this repo and run `labs/track1-incident-workflow/setup-local.sh`. The script builds the MCP servers, writes this folder's absolute paths into `.bob/mcp.json` (Bob needs absolute paths), points `DOCKER_HOST` at Podman or Colima, runs `terraform init` and pre-pulls the images. Run it again if you move the folder.
- **Track 2 Java:** install SDKMAN, Java 8 and Maven, clone this repo and warm Maven's cache, open the lab folder in Bob, then pick the right team and install the Premium Package for Java.
- **Bonus 2b Liberty:** as Track 2, plus Semeru 21 installed alongside Java 8 (not the default) — the AMA recipes move the project to Java 21.
- **Track 3 Z:** clone [Bank of Z](https://github.com/IBM/Bank-of-Z), upgrade Bob to 2.2.0 or newer if needed (the Premium Package for Z requires it), and install the Premium Package for Z.

On the TechZone VM, Bob's first launch also asks for a keyring password, to trust the folder, and to sign in; each guide lists those prompts.

Track 1 needs no ServiceNow instance or credentials. A ServiceNow-compatible Local Service Desk runs on `http://localhost:8099`, and pointing the lab at a real instance is a config change described in the guide.

### Instructors: checking a VM

Run a track's setup on one VM before the session and check:

- **Track 1:** Bob ⚙ → MCP shows **servicenow**, **terraform** and **ansible** as **Connected** with `labs/track1-incident-workflow` open.
- **Track 2 Java:** Bob ⚙ → General shows the workshop **Team** with **Premium Package for Java Modernization** installed, and Bob's ▶ workflow list shows **Java Modernization** with `labs/track2-java-upgrade` open.
- **Bonus 2b Liberty:** as Track 2, with `labs/track2b-liberty-replatforming` open; `ls ~/.sdkman/candidates/java` lists an `8.0.x-zulu` and a `21.x-sem`.
- **Track 3 Z:** Bob ⚙ shows version 2.2.0 or newer, and **Z Code** and **Z Architect** appear in the mode selector.

## Resetting between runs

Bob edits files during the labs. Restore a lab folder with `git checkout -- <folder> && git clean -fd <folder>`. For Track 1, run `./demo-scripts/bank-app/shutdown-flow.sh` first.

## Sample credentials

The sample applications ship with demo logins (for example `demo` / `demo123` for the Track 1 bank app) and seeded password hashes. These are test fixtures for local containers, not real credentials.

## Sources

- Track 1 is adapted from the `bob-incidents-workflow` lab. Changes: the Local Service Desk replaces a hosted ServiceNow instance; no credentials are in the repo; script paths are fixed; and the Docker socket is detected automatically.
- Track 2 Java is the lab 2 starting snapshot from the Bob Java modernization labs.
- Track 3 Z uses [IBM/Bank-of-Z](https://github.com/IBM/Bank-of-Z).

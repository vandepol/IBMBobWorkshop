# IBM Bob — Java Upgrade Lab
## Simple Pharmacy: Java 8 → Java 21 and Jakarta EE 10, running on Liberty

<sub>⏱ About 80 minutes in total · Bob tier: Premium Package for Java · Verified on IBM Bob 2.2.1, 2 October 2026</sub>

---

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [What to watch for](#what-to-watch-for)
4. [The Java Modernization workflow](#the-java-modernization-workflow)
5. [Setting up](#setting-up)
6. [Exercise 1: Run the Java Upgrade workflow](#exercise-1-run-the-java-upgrade-workflow)
7. [Exercise 2: Read what changed](#exercise-2-read-what-changed)
8. [Exercise 3: Run the upgraded application](#exercise-3-run-the-upgraded-application)
9. [Troubleshooting](#troubleshooting)
10. [Conclusion](#conclusion)

---

# Introduction

### The application

The **Simple Pharmacy Management System** is a small Struts web application that manages:
- **Prescriptions** — create and validate patient prescriptions
- **Orders** — process medication orders and payments
- **Medicines** — the medicine inventory
- **Dashboard** — pending prescriptions and orders at a glance

### What a Java upgrade involves

Moving an application from Java 8 to a current LTS release is rarely just a compiler flag. It usually means:
- **Namespace changes** — `javax.*` (Java EE) becomes `jakarta.*` (Jakarta EE)
- **Dependency upgrades** — libraries that pin old Java versions, or carry known vulnerabilities
- **Build and configuration updates** — compiler settings, plugin versions, deployment descriptors
- **Framework migrations** — frameworks that changed their own APIs to support Jakarta EE

## About this lab

You will use **Bob's Java Modernization workflow** (Java Upgrade) to move the pharmacy app to Java 21 and Jakarta EE 10, then use Bob's chat to finish the job and get the application running on Liberty.

| | Before | After |
|---|---|---|
| Java | 8 | **21** (IBM Semeru) |
| Enterprise APIs | Java EE 7 (`javax.*`) | **Jakarta EE 10** (`jakarta.*`) |
| Struts | 2.5.33 | **7.x** |
| Liberty features | `servlet-3.1`, `jsp-2.3` | **`servlet-6.0`, `pages-3.1`** |
| Known vulnerabilities | 10 | **0** |

## Learning objectives

By the end of this lab you will have:
- Run a structured, phased modernization workflow — **Analyze → Upgrade → Validate** — not a chat prompt
- Approved dependency fixes after reading Bob's root-cause analysis and the exact diff
- Seen a version upgrade turn into a vulnerability remediation, with advisory IDs
- Read Bob's per-task cost breakdown
- Taken the result from *builds* to *runs*, and understood why those are two different milestones

---

# Prerequisites

<sub>⏱ About 15 minutes — do this before the session. On the sandbox VM it has already been done.</sub>

### 1. IBM Bob
- IBM Bob IDE installed and signed in
- A Bob team that includes the **Premium Package for Java** (Bob ⚙ → *General* lists it under *Add-ons*)

### 2. A modern Bash (macOS only)

SDKMAN's installer needs **Bash 4 or newer**; macOS ships Bash 3.2. Without this step the install stops with *"SDKMAN requires Bash 4 or higher"*, and Bob's own **Install SDKMan** button fails the same way.

```bash
brew install bash        # needs Homebrew (admin rights); on a locked-down laptop use the sandbox VM
bash --version           # should report 5.x — open a new terminal first
```

### 3. SDKMAN, Java 8 and Maven

SDKMAN is **required**: the workflow checks for it before it will continue (Windows uses WinGet instead).

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Newest Zulu 8 build — pinned identifiers like 8.0.492-zulu get withdrawn over time
JAVA8=$(sdk list java | grep -o '8\.0\.[0-9]*[^ ]*-zulu' | grep -v fx | sort -V | tail -1)
sdk install java "$JAVA8"
sdk default java "$JAVA8"    # 'default', not 'use': Bob runs builds in its own shell
sdk install maven
```

Don't install Java 21 yourself — Bob offers to install it during the lab. (If a Java 21 is already installed, Bob uses it and skips that step; the lab still works.)

### 4. The lab code, with Maven's cache warmed

```bash
git clone https://github.com/vandepol/IBMBobWorkshop
cd IBMBobWorkshop/labs/track2-java-modernization/Bobathon/labs/lab2-java-upgrade/snapB-java-upgrade
mvn -B dependency:go-offline && mvn -B clean
```

### 5. Restart Bob

Fully quit and reopen Bob so it picks up SDKMAN, Java and Maven.

> No Docker is needed. Liberty is downloaded by the Liberty Maven plugin the first time you run the app.

---

# What to watch for

- **Bob installs the JDK itself** when Java 21 is missing — environment management, not just code changes.
- **OpenRewrite recipes** applied across the codebase: `javax` → `jakarta`, compiler settings, plugins.
- **A diagnosed dependency fix** — root cause, a named fix, and a diff, waiting for your approval.
- **A vulnerability scan mid-migration**, and a remediation that lists every advisory it closed.
- **A per-task cost breakdown** in the final summary.

---

# The Java Modernization workflow

* **Analyze** — Bob inspects the project, checks dependencies for known vulnerabilities and runs a baseline build
* **Upgrade** — Bob applies OpenRewrite recipes, then works through build issues and vulnerabilities with your approval
* **Validate** — Bob rebuilds, cross-checks every fix and produces a visual summary with costs

## Approvals

Bob asks before it runs commands or edits files:
- **Approve once** — this action only
- **Approve for task** — this command for the rest of the task
- **Approve subtask tools for task / Approve edit tools for task** — a sub-agent's tools or edits for the rest of the task
- Shell commands may carry a **Security warning**: tick **I understand the risk**, then **Approve**

> 💡 **Don't type in the chat while the workflow is running.** A message is sent to whichever sub-agent is active and cancels any command that is waiting for approval.

---

# Setting up

<sub>⏱ About 5 minutes</sub>

### 1. Smoke test

```bash
java -version    # 1.8.0_xxx — Java 8 is the starting point
mvn -version     # 3.6+, and its "Java version" line should also say 1.8
sdk version      # SDKMAN must be present
```

### 2. Open the snapshot folder as the project root

In Bob: **File → Open Folder** → `snapB-java-upgrade`

```
IBMBobWorkshop/labs/track2-java-modernization/Bobathon/labs/lab2-java-upgrade/snapB-java-upgrade
```

> 🚩 Open **`snapB-java-upgrade`**, not the parent `lab2-java-upgrade`. The workflow only appears at the snapshot level.

You may see a few first-run pop-ups — *Install GitHub Copilot modernization extension*, *C++ IntelliSense*, *open the parent git repository*. They aren't part of the lab: choose **Not Now** / **Never**.

### 3. Confirm the workflow is available

Click **▶** at the top of the Bob panel. If Bob asks which workspace to use, pick **snapB-java-upgrade**. **Java Modernization** should be in the list.

![Bob workflows list with Java Modernization](images/track2-java/01-workflow-list.png)

> 🚩 **No Java Modernization?** Your Bob team doesn't include the Premium Package for Java.

---

# Exercise 1: Run the Java Upgrade workflow

### 1. Start the workflow
<sub>⏱ About 1 minute</sub>

Click **▶ Start** next to **Java Modernization**. The workflow opens on a short *Getting Started* card — worth ten seconds.

### 2. Analyze the project
<sub>⏱ About 3 minutes</sub>

- **Select Project** already points at `snapB-java-upgrade`
- Leave **Custom project path** and **Custom build command** off
- Click **Continue**

![Analyze Project panel](images/track2-java/02-analyze-project.png)

Bob detects the dependencies and asks to **query vulnerabilities** — approve it. It finds about ten, mostly in Struts 2 and Apache Commons. Then it asks to run a **baseline build** (`mvn clean install`) — approve that too. Bob proves the application compiles *before* anything changes, so nobody argues later about whether it was already broken.

While it builds, open `pom.xml` and find the `maven-compiler-plugin`: `<source>1.8</source>` and `<target>1.8</target>`.

### 3. Choose the modernization type
<sub>⏱ About 1 minute</sub>

- Select **Java Upgrade**
- Switch **Enable Git Flow** **off** — it is on by default, and branch management is outside this lab
- Click **Continue**

![Flow Selection with Java Upgrade selected and Git Flow off](images/track2-java/03-flow-selection.png)

*Liberty Modernization* is greyed out ("Application is already using Liberty"); *UI Modernization* and *Java Unit Testing* are other labs in this repo.

### 4. Prerequisite check
<sub>⏱ About 1 minute (longer if SDKMAN needs installing)</sub>

The workflow expands to 14 steps and first checks for SDKMAN. Approve the `sdk version` command. If SDKMAN is missing, Bob offers **Install SDKMan**. On a Mac that only works once a modern Bash is installed:

![SDKMAN install failing on Bash 3.2](images/track2-java/04-sdkman-bash-error.png)

Run `brew install bash` in a terminal, then click **Retry**.

### 5. Configure the upgrade
<sub>⏱ About 2 minutes</sub>

| Setting | Value |
|---|---|
| Java Distribution | **Semeru (IBM)** (preselected) |
| Java Version | **Java 21** |
| Jakarta EE Version | **Jakarta EE 10** |

Bob describes the upgrade path, rates its complexity and estimates how many vulnerabilities the upgrade can resolve. Read it — you'll come back to it in Exercise 3. Click **Continue**.

![Java Upgrade Configuration with recommendations](images/track2-java/05-upgrade-config.png)

> If Java 21 isn't installed, Bob offers **Install** and sets it up through SDKMAN, then resumes. If it's already installed, Bob goes straight on.

### 6. Run the recipes
<sub>⏱ About 2 minutes</sub>

Bob proposes two OpenRewrite recipes — `UpgradeToJava21` and `JakartaEE10`. **Approve once.** In about a minute nine files change:
- `pom.xml` — `javax.servlet`/`javax.servlet.jsp` APIs become `jakarta.*`, the compiler moves to `<release>21</release>`, plugins are upgraded
- `web.xml` — moves to the Jakarta EE namespace, version 6.0
- seven Java classes — `@Serial` added to `serialVersionUID`

![Run Rewrite Recipes approval](images/track2-java/06-run-recipes.png)

Bob then rebuilds under Java 21 (approve it): *0 errors, 1 warning*.

### 7. Approve the Javassist fix
<sub>⏱ About 5 minutes</sub>

Bob starts a **Fix build issues** sub-agent for the warning. Watch how it works:
1. Reads `pom.xml` and explains the cause — `javassist 3.20.0-GA`, pulled in through `struts2-core → ognl`, has POM problems on Java 21
2. Runs `mvn dependency:tree` to confirm before changing anything
3. Proposes a `<dependencyManagement>` block pinning javassist to `3.29.2-GA`, and asks

![Javassist root cause and Yes/No approval](images/track2-java/07-javassist-approval.png)

Choose **Yes, proceed**. Bob then opens a side-by-side diff of `pom.xml` and waits for **Approve once** on *Apply Diff*:

![pom.xml diff preview before the edit is applied](images/track2-java/08-javassist-diff.png)

**Read the rationale before you approve.** A find-and-replace tool would have given you a broken build and an error log. This gave you a diagnosis, a named fix and the exact diff, then waited for a human. Bob re-runs the build and summarises *Change / Root cause / Fix / Validation* in a table.

### 8. Fix the vulnerabilities
<sub>⏱ About 10 minutes</sub>

The workflow hands over to **Java Vulnerability Remediation**, rescans, and asks:

![Vulnerabilities fixing prompt](images/track2-java/09-cve-confirm.png)

Choose **Yes, resolve all**. Bob plans the fixes (under a minute), then runs a **Fix Vulnerabilities** sub-agent. Approving its tools for the task keeps it moving. Expect:
- Struts `2.5.33` → `6.8.0`
- `<dependencyManagement>` overrides for commons-fileupload, commons-io, FreeMarker and commons-lang3, each commented with its GitHub advisory ID

> 🚩 When a sub-agent finishes its write-up it can stop with an **End subtask** link at the bottom right and no spinner. Click **End subtask** — the workflow continues by itself.
>
> ![End subtask link after the Fix Vulnerabilities write-up](images/track2-java/10-end-subtask.png)

A **Validate Fixes** sub-agent then cross-checks every advisory and ends with *"10 of 10 CVEs addressed"* and `BUILD SUCCESS`:

![Vulnerability fix validation results](images/track2-java/11-cve-validation.png)

A version upgrade has just turned into a vulnerability remediation, without anyone scoping it as one.

### 9. Final build and summary
<sub>⏱ About 5 minutes</sub>

A **Final step** sub-agent runs `mvn clean compile` under Java 21 (approve it). Then Bob generates a visual modernization summary and a per-task cost breakdown:

![Workflow summary with per-task costs](images/track2-java/12-summary.png)

| Look for | Expected |
|---|---|
| Java version | 1.8 → **21**, Semeru |
| Jakarta EE | **10** |
| Build | completed successfully |
| Security | 10 of 10 advisories resolved |
| Cost | about **1.5–5 Bob coins**, itemised per subtask (1.69 in the verification run) |

### ✋ Checkpoint — everyone has a successful build and a summary on screen

---

# Exercise 2: Read what changed

<sub>⏱ About 5 minutes</sub>

Click **Show all** next to *9 files changed* at the bottom of the Bob panel.

![Bob Edits diff view of pom.xml](images/track2-java/13-show-all-diff.png)

- **`pom.xml`** — `jakarta.servlet-api 6.0.0`, `jakarta.servlet.jsp-api 3.1.1`, Struts `6.8.0`, `<release>21</release>`, and the `<dependencyManagement>` block with the Javassist pin and CVE overrides
- **`web.xml`** — Jakarta EE namespace, version 6.0
- **Action classes** — they never imported `javax.*`, so there's no import swap; the recipe added `@Serial`

Discuss before moving on:
- Would you merge this change?
- How long would this have taken your team, on one application? How many applications does that multiply by?
- Where would you still want a human gate, and did the approvals put one there?

---

# Exercise 3: Run the upgraded application

The build is green. The real test is whether the application runs.

### 1. Start Liberty
<sub>⏱ About 5 minutes (the first run downloads Liberty)</sub>

Make sure Maven is on Java 21 (`mvn -version`; open a new terminal if it still says 1.8), then from the project folder:

```bash
mvn liberty:run
```

Liberty starts, but the application doesn't:

```
CWWKZ0002E: An exception occurred while starting the application simple-pharmacy.war ...
CWWKC2263E: The webapp : WEB-INF/web.xml deployment descriptor on line 5 specifies version 60,
which is higher than the current provisioned version 31.
```

Press **Ctrl+C** to stop the server (or run `./stop-liberty.sh`).

### 2. Why the build passed but the app doesn't start
<sub>⏱ About 5 minutes</sub>

The Java Upgrade workflow's job ends at a successful build: it rewrites the code, resolves dependencies and vulnerabilities, and proves the result with Maven. Two things sit outside a build, and they only show up when the application is deployed:

1. **Server configuration.** `src/main/liberty/config/server.xml` still asks Liberty for the Java EE 7 features (`servlet-3.1`, `jsp-2.3`). The upgraded `web.xml` declares Jakarta EE 10, so Liberty refuses it.
2. **Framework generation.** Struts **6.x** is the newest line that keeps the old programming model, which is why the vulnerability fix chose 6.8.0: it closes every advisory and the action classes compile unchanged. But Struts 6 is still built on `javax.servlet`. **Struts 7** is the first release built on `jakarta.servlet` (Jakarta EE 10), and it is a migration rather than a version bump:
   - `ActionSupport` moved from `com.opensymphony.xwork2` to `org.apache.struts2`
   - Request parameters only reach setters annotated `@StrutsParameter`
   - JSP expressions may only read classes on an OGNL *allowlist*

None of these appear at compile time — the app's own code never touches the servlet API — so the build is genuinely green. This is normal in modernization work: **build-green and deploy-green are two milestones**, and this exercise is the second one.

### 3. Ask Bob to finish the migration
<sub>⏱ About 15 minutes</sub>

In Bob's chat (with no workflow running), paste:

```
The application builds, but Liberty fails to start it:

CWWKC2263E: The webapp : WEB-INF/web.xml deployment descriptor on line 5 specifies version 60,
which is higher than the current provisioned version 31.

The project is now on Java 21 and Jakarta EE 10. Make it run on Liberty:
update server.xml to the Jakarta EE 10 features, move Struts to a Jakarta-based 7.x release,
and make the code and configuration changes Struts 7 needs. Show me each change before applying it.
```

Review each proposal against this checklist — these are the changes that made the app run in the verification run:

| File | Change | Why |
|---|---|---|
| `server.xml` | `servlet-3.1` → `servlet-6.0`, `jsp-2.3` → `pages-3.1` | Liberty must provide Jakarta EE 10 |
| `pom.xml` | `struts2.version` → `7.4.0`; remove the unused `javax.servlet:jstl` | Struts 7 is built on `jakarta.servlet`; no JSP uses JSTL |
| 4 action classes | `import org.apache.struts2.ActionSupport;` | The class moved in Struts 7 |
| 4 action classes | `@StrutsParameter` on every setter that receives a request parameter | Without it, forms and detail pages lose their input |
| `struts.xml` | allowlist `com.pharmacy.model` and the `java.util` collections | Without it, JSPs show blank fields and an empty dashboard list |

<details>
<summary>Reference: the exact edits</summary>

`src/main/liberty/config/server.xml`
```xml
<featureManager>
    <feature>servlet-6.0</feature>
    <feature>pages-3.1</feature>
    <feature>jndi-1.0</feature>
</featureManager>
```

`pom.xml`
```xml
<struts2.version>7.4.0</struts2.version>
<!-- and delete the javax.servlet:jstl 1.2 dependency -->
```

Each action class (`DashboardAction`, `MedicineAction`, `OrderAction`, `PrescriptionAction`)
```java
import org.apache.struts2.ActionSupport;
import org.apache.struts2.interceptor.parameter.StrutsParameter;   // classes with setters

    @StrutsParameter
    public void setPrescriptionId(String prescriptionId) { ... }    // and every other setter
```

`src/main/resources/struts.xml`
```xml
<constant name="struts.allowlist.packageNames" value="com.pharmacy.model" />
<constant name="struts.allowlist.classes" value="java.util.ArrayList,java.util.List,java.util.Collection" />
```
</details>

> The allowlist and `@StrutsParameter` are Struts 7 security features — they're why Struts 7 closes whole classes of OGNL vulnerabilities. Allow what the app needs; don't switch them off.

### 4. Verify the application
<sub>⏱ About 5 minutes</sub>

```bash
mvn clean package && mvn liberty:run
```

Expect `CWWKZ0001I: Application simple-pharmacy.war started`, then open:

| Page | URL |
|---|---|
| Dashboard | http://localhost:9081/simple-pharmacy.war/dashboard |
| Prescriptions | http://localhost:9081/simple-pharmacy.war/prescription-list |
| Orders | http://localhost:9081/simple-pharmacy.war/order-list |
| Medicines | http://localhost:9081/simple-pharmacy.war/medicine-list |

Check that it behaves like the original:
- The dashboard shows **3** prescriptions with **RX001** pending
- **View** on a prescription shows the patient, doctor and medicine (not blank fields)
- **Create Prescription** offers 9 medicines, saves, and the new row appears in the list
- **Validate** on RX001 removes it from the dashboard's pending list

> The context root includes `.war` because `server.xml` doesn't set one. The bare root URL shows a Struts "Problem Report" page in the original app too — start from `/dashboard`.

### ✋ Checkpoint — the dashboard is running on Java 21, Jakarta EE 10 and Struts 7

---

# Troubleshooting

| Symptom | What to do |
|---|---|
| No **Java Modernization** in the workflow list | Your Bob team lacks the Premium Package for Java, or you opened the parent folder instead of `snapB-java-upgrade` |
| *"SDKMAN requires Bash 4 or higher"* / *"Failed to install SDKMan: true"* | macOS ships Bash 3.2: `brew install bash`, open a new terminal, click **Retry** |
| `sdk install java 8.0.xxx-zulu` → *not a valid candidate version* | Use the newest `8.0.x-zulu` from `sdk list java` |
| Only **Java 25** offered in *Java Version* | The project is already on Java 21 from an earlier run. Reset it: `git checkout -- . && git clean -fd` in the lab folder, or re-extract the folder if you downloaded a zip |
| Agent steps finish instantly, *"Request Failed — An unexpected error occurred"*, Usage stays at `0.0000` | Bob's session is stale. **Sign out of Bob and sign back in**, then retry |
| A sub-agent's write-up ends and nothing happens | Click **End subtask** at the bottom right |
| *Command cancelled* | Something was typed in chat while a command awaited approval. Ask Bob to continue, then approve again |
| `CWWKC2263E … version 60 … higher than … 31` | Exercise 3: Liberty still has the Java EE 7 features |
| `SRVE0321E: The [struts2] filter did not load` and every page returns 500 | Struts 6 on a Jakarta EE 10 server: move to Struts 7 (Exercise 3) |
| *"Parameter injection for method [setX] … rejected"* on a page | Add `@StrutsParameter` to that setter |
| Detail pages show blank fields, or the dashboard says *No pending prescriptions* | Add the `struts.allowlist.*` constants to `struts.xml` |
| `mvn` reports Java 1.8 after the upgrade | Open a new terminal, or `sdk use java 21.0.12-sem` (SDKMAN Semeru IDs end in `-sem`) |
| Port 9081 already in use | `./stop-liberty.sh` in the project folder |

---

# Conclusion

You have:
- ✅ Run Bob's Java Modernization workflow from analysis to summary
- ✅ Approved a diagnosed dependency fix after reading the diff
- ✅ Turned a Java upgrade into a vulnerability remediation — 10 advisories closed, each one named
- ✅ Read a per-task cost breakdown
- ✅ Taken the application from *builds* on Java 21 to *runs* on Jakarta EE 10 and Struts 7

Bring one surprise and one criticism to the regroup. The criticism is the more useful of the two.

---

# Optional extras

- **Audit the namespace migration** — ask Bob: *"Audit all imports and configuration for remaining `javax.*` references that should be `jakarta.*`."*
- **Lab 4 — unit test generation** (`Bobathon/labs/lab4-unit-test-generation/LAB4-GUIDE.md`). For a quick run, set *Candidate Selection Strategy* to the `com.pharmacy.repository` package.
- **Lab 5 — security vulnerability remediation** (`Bobathon/labs/lab5-security-vulnerability-remediation/LAB5-GUIDE.md`) — SQL injection, XSS and input validation.
- **Lab 3 — UI modernization** (`Bobathon/labs/lab3-ui-modernization/LAB3-GUIDE.md`). Needs Node.js/npm and Docker; the guide builds a React + Material UI front end, not Angular.
- **Lab 1 — WebSphere to Liberty** (`Bobathon/labs/lab1-java-liberty-replatforming/LAB1-GUIDE.md`). In Bob 2.2.1 the option is called *Liberty Modernization*; open the `snapA-java-liberty-replatforming` folder.

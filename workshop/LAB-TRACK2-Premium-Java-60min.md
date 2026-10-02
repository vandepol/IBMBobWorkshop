# Track 2 — IBM Bob, Premium Package for Java
## Lab: Java 8 to Java 21, done by an agent that shows its work

**Time:** 60 minutes
**Environment:** pre-baked sandbox VM — nothing to install. Or your own laptop — see *Running on your own laptop* below.
**Bob tier:** Premium (includes the Java Modernization workflow suite)

> Based on `labs/track2-java-modernization/Bobathon/labs/lab2-java-upgrade/LAB2-GUIDE.md`, cut to 60 minutes with checkpoints.

---

## What you are about to do

The Simple Pharmacy application runs on Liberty, on **Java 8**, with Struts. You are going to put it on **Java 21** with **Jakarta EE 10** — namespace migration, dependency conflicts, CVE scan and all — in under an hour, using Bob's Java Modernization workflow.

This is the lab that matters most to a bank with a large legacy Java estate, because the thing standing between you and a supported runtime is rarely the code change itself. It is the long tail of dependency breakage nobody wants to own.

**By the end you will have seen:**
- A structured, phased workflow — **Analyze → Upgrade → Validate** — not a chat window
- Bob **installing the target JDK itself** when it finds it missing
- **OpenRewrite recipes** applied across the codebase, `javax.*` → `jakarta.*`
- Bob hitting a genuinely broken dependency, diagnosing the root cause, and proposing a specific fix **for your approval**
- A **CVE scan** mid-migration with a remediation prompt
- A green build, and a summary telling you what it cost

---

## Running on your own laptop

Do this **before** the session; it downloads a JDK and Maven dependencies. On the sandbox VM it has already been done.

**macOS only — do this first.** SDKMAN's installer needs Bash 4 or newer, and macOS still ships Bash 3.2, so on a stock Mac `curl … | bash` stops with *"SDKMAN requires Bash 4 or higher"*. Bob's own **Install SDKMan** button runs the same command and fails the same way, with the less helpful *"Failed to install SDKMan: true"*. Install a modern Bash with Homebrew, then open a new terminal so it comes first on your `PATH`:

```bash
brew install bash      # Homebrew needs admin rights; on a locked-down laptop ask IT, or use the sandbox VM
bash --version         # should report 5.x
```

```bash
# SDKMAN, Java 8 (the lab's starting point) and Maven
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# Pick the newest Zulu 8 build SDKMAN lists. Pinned identifiers go stale
# (8.0.492-zulu is gone); Temurin 8 is not available on Apple Silicon.
JAVA8=$(sdk list java | grep -o '8\.0\.[0-9]*[^ ]*-zulu' | grep -v fx | sort -V | tail -1)
sdk install java "$JAVA8"
sdk default java "$JAVA8"
sdk install maven

# The lab code, with Maven's cache warmed so the first build isn't a cold download
git clone https://github.com/vandepol/IBMBobWorkshop
cd IBMBobWorkshop/labs/track2-java-modernization/Bobathon/labs/lab2-java-upgrade/snapB-java-upgrade
mvn -B dependency:go-offline && mvn -B clean
```

SDKMAN is **required**, not optional: the workflow's first step checks for it and will not continue without it. On Windows the workflow uses WinGet instead.

Don't install Java 21 yourself: Bob offers to install it during the lab, and that is one of the moments worth seeing. If a Java 21 is **already** installed (for example in `/Library/Java/JavaVirtualMachines`), Bob uses it and skips the install step — the lab still works, you just won't see that moment. Then **fully quit and restart Bob** so it picks up the SDKMAN tools. Your Bob account needs the **Premium Package for Java** entitlement.

No Docker is needed for this lab. Optional extra E (Lab 3) is the only part that uses it.

---

## 0:00–0:05 · Smoke test

Everything is pre-installed. This catches a bad image while there is still time.

```bash
java -version    # 1.8.0_xxx, Zulu — Java 8 is the STARTING state
mvn -version     # 3.6+, and its "Java version" line should also say 1.8
sdk version      # SDKMAN must be present — the workflow refuses to continue without it
```

Then in Bob ⚙ **Settings → General**, note **Usage**. After the first agent step it should be above `0.0000`. If agent steps finish instantly, change nothing and Usage stays at zero, Bob's model access is failing (the log shows `Forbidden`) — see Troubleshooting.

In your IDE, open **this exact folder** as the project root:

```
IBMBobWorkshop/labs/track2-java-modernization/Bobathon/labs/lab2-java-upgrade/snapB-java-upgrade
```

(On the sandbox VM the repo is at `~/workshop/IBMBobWorkshop`.)

> 🚩 **It must be the `snapB-java-upgrade` folder, not the parent `lab2-java-upgrade` folder.** The Java Modernization workflow only appears when Bob is opened at the snapshot level. This is the most common way to lose ten minutes on this lab.

Then confirm, in Bob's chat panel:
- The mode indicator at the bottom reads **Agent**
- Pressing the **▶** button at the top shows **Java Modernization** in the workflow list

> 🚩 **No Java Modernization workflow?** Your account lacks the Premium entitlement. Raise your hand now — this lab cannot run without it, and there is a standard-Bob track you can join.

### ✋ CHECKPOINT 1 — *everyone sees the Java Modernization workflow*

---

## 0:05–0:09 · Start the workflow

1. Click the **▶** button at the top of the Bob window
2. If Bob asks which workspace to run the workflow in, pick **snapB-java-upgrade**
3. Find **Java Modernization** in the list and click its **▶ Start**

The workflow opens on a short **Getting Started** card (Java Versions, Git Flow, Application Transformation, Error Handling) — worth a ten-second read.

> 💡 Bob will ask you to approve tool calls throughout: vulnerability queries, builds, shell commands. Shell commands carry a **Security warning**; tick **I understand the risk**, then **Approve**. If you started with only *Read* auto-approval, expect a prompt every minute or so.

Notice what this is not: you did not describe your task in a sentence and hope. You launched a defined process with phases, inputs and a summary at the end. That distinction is most of the enterprise argument for the premium package.

---

## 0:09–0:17 · Analyze

**Analyze Project**
- **Select Project** should already show the `snapB-java-upgrade` folder. Confirm it.
- Leave **Custom project path** and **Custom build command** off.
- Click **Continue**.

Bob now detects the dependencies, queries them for known vulnerabilities (approve it — it finds about ten, mostly in Struts 2 and Apache Commons), and **runs a baseline build** (`mvn clean install`; approve that too). It is establishing that the application compiles *before* it changes anything — so that if something breaks later, there is no argument about whether it was already broken.

> ⏱️ With `~/.m2` pre-warmed the baseline build takes seconds; on the sandbox the whole step should finish in under three minutes. Considerably longer means the warm cache did not make it into the image — tell your track lead.

While it builds, open `pom.xml` and find the `maven-compiler-plugin` configuration: `<source>1.8</source>` and `<target>1.8</target>`. Java 8. Remember what they say.

---

## 0:17–0:20 · Choose what kind of modernization

**Modernization Type** (the step is labelled *Flow Selection*)
- Select **Java Upgrade**
- **Enable Git Flow**: switch it **off** — it is **on** by default *(branch management is outside the scope of this lab)*
- Click **Continue**

Note the other options on this screen — **UI Modernization** and **Java Unit Testing** are separate labs in the same repo, both available to you afterwards. **Liberty Modernization** is greyed out with *"Application is already using Liberty"* — that is Lab 1, which starts from an earlier snapshot.

The workflow now expands to 14 steps. Its first, **Check Java prerequisites**, looks for SDKMAN. If SDKMAN is missing Bob offers **Install SDKMan**; on a Mac that only works once a modern Bash is installed (see *Running on your own laptop*). After installing Bash, click **Retry**.

---

## 0:20–0:40 · Configure the upgrade and run the recipes

This is the longest block in the lab and the one with the most to watch.

**Java Upgrade Configuration**
- Java Distribution: **Semeru (IBM)** (preselected)
- Java Version: **Java 21**
- Jakarta EE Version: **Jakarta EE 10** (a dropdown offering *Do Not upgrade* and EE 8, 9, 10, 11)
- A *"Recommendations Unavailable"* note may appear — that is expected for this project
- Click **Continue**

### The first thing to watch for

If Java 21 Semeru is not installed, Bob tells you so and offers an **Install** button. Click it. (If a JDK 21 is already on the machine, Bob skips straight to the recipes.)

**Watch what happens.** Bob installs a JDK via SDKMAN, on its own, and then resumes the workflow where it left off. It did not fail and hand you a task. It did not tell you to go and read an installation guide. It noticed a missing prerequisite in its own environment and fixed it.

Stop and register that, because it reframes what this tool is. Everything else in this lab is code transformation; this is environment management.

### Then the recipes run

Bob asks to run two OpenRewrite recipes — `UpgradeToJava21` and `jakarta.JakartaEE10` — then applies them across the codebase (about a minute) and begins an agentic build pass. You will see:
- `javax.servlet` / `javax.servlet.jsp` dependencies in `pom.xml` replaced with their `jakarta.*` equivalents
- `web.xml` moved to the Jakarta EE namespace, version 6.0
- Compiler configuration moved to `<release>21</release>`, plugins upgraded
- `@Serial` added to `serialVersionUID` fields in the Java classes
- A rebuild, then a sub-agent that works through any fallout

Let it run. It will not be silent and it will not be instant.

### ✋ CHECKPOINT 2 — *recipes have run and Bob is working through build issues*

---

## 0:40–0:48 · Approve the fixes

Bob now works the dependency problems **one at a time**, and asks before each change.

### The one to pay attention to: Javassist

The project depends on `javassist 3.20.0-GA`, whose POM is malformed in a way that breaks the Java 21 build. Bob will:
1. Explain the root cause — not just "the build failed"
2. Propose a specific fix: a `<dependencyManagement>` block pinning javassist to a version whose POM parses
3. Ask you to approve it

> ℹ️ In the 2 October 2026 verification run (Bob 2.2.1, warm `~/.m2`), the post-recipe build reported **0 errors and 1 warning**, so no Javassist prompt appeared. Whether you see it depends on what the build trips over. If it doesn't come up, use the time on the CVE scan below and on the diff in the debrief.

**Read the rationale before you approve.** This is the moment the lab is built around. A find-and-replace tool gives you a broken build and an error log. This gave you a diagnosis and a named fix, and then waited for a human to agree.

Where Bob offers *"show me the exact edits before applying them"*, take it at least once. Seeing the diff before it touches your `pom.xml` is the answer to half the questions your architecture review board will ask.

### The CVE scan

After the build pass, the workflow hands over to **Java Vulnerability Remediation**. Bob scans the dependencies again (about ten findings), then asks *"Do you want to proceed with fixing the detected vulnerabilities?"* with **Yes, resolve all** / **Yes, select subset** / **No**. Choose **Yes, resolve all**.

Afterwards, check `pom.xml`: the Struts 2 version should have moved off `2.5.33`. If it hasn't, and the *Fix Vulnerabilities* subtask "completed" in a second or two, the agent never ran — see *Agent steps finish instantly* in Troubleshooting.

Consider what just happened: a version upgrade turned into a vulnerability remediation without you scoping it as one. For most institutions those are two separate programmes of work with two separate business cases.

---

## 0:48–0:54 · Green build, and what it cost

**Validation** — Bob runs `mvn clean install` under Java 21. You are looking for:

```
BUILD SUCCESS
```

**Modernization Summary** — Bob then generates a visual summary. Read it properly:

| What to look for | Expected |
|---|---|
| Java version | 1.8 → **21**, IBM Semeru |
| Jakarta EE | **10** applied |
| Build | No errors |
| Security | Vulnerabilities resolved |
| Changes | Listed as *N files changed* (committed only if Git Flow was on — it is off in this lab) |
| **Cost** | Typically **3–5 Bob coins**, with a per-task breakdown |

That last row is the one to linger on. The tool is telling you what the work cost, itemised by subtask. Whatever you think of the number, an AI vendor putting a per-task meter in front of you is not the norm, and it is what makes a real business case arithmetically possible rather than a matter of faith.

### ✋ CHECKPOINT 3 — *everyone has BUILD SUCCESS and a summary on screen*

---

## 0:54–1:00 · Read what actually changed, then debrief

Click **Show all** next to *N files changed* at the bottom of the Bob panel, or open the files directly:

- `pom.xml` — `javax.servlet-api 3.1.0` is now `jakarta.servlet-api 6.0.0`, `javax.servlet.jsp-api` is now `jakarta.servlet.jsp-api 3.1.1`, the compiler plugin reads `<release>21</release>`, and any dependency overrides Bob added for Javassist or CVEs are here too.
- `src/main/webapp/WEB-INF/web.xml` — the `xmlns.jcp.org/xml/ns/javaee` 3.1 descriptor is now `jakarta.ee/xml/ns/jakartaee` 6.0.
- `src/main/java/com/pharmacy/action/DashboardAction.java` — the Struts actions never imported `javax.*`, so there is no import swap to see; the recipe added `@Serial` to `serialVersionUID`.

Then ask the awkward question: `javax.servlet:jstl 1.2` and Struts 2.5 are both still `javax`-based. The build is green; will the app actually *run* on a Jakarta EE 10 server? Optional extra A answers that.

This is the part that decides whether you trust it. Not the summary graphic — the diff.

**Take two minutes on these before the regroup:**
- Would you have merged this change?
- How long would this upgrade have taken your team, on one application?
- How many applications does that multiply by?
- Where would you still want a human gate, and did the approval flow put one there?

Bring one surprise and one criticism to the regroup. The criticism is the more useful of the two.

---

# Optional extras

Take these home. The sandbox is yours to keep.

## A · Run the upgraded application *(~15 min)*

Cut from the live lab because Liberty startup is variable and it is where rooms desynchronise. It is worth doing at your own pace.

Ask Bob:

```
Provide me with the commands to run this application.
```

Then start the server and open:

```
http://localhost:9081/simple-pharmacy.war/dashboard
```

`server.xml` deploys the app with no explicit context root, so the path includes `.war` (as `run-liberty.sh` prints). If Bob adds `context-root="/simple-pharmacy"`, use `http://localhost:9081/simple-pharmacy/dashboard` instead.

Liberty itself is downloaded by the Liberty Maven plugin the first time — no Docker required.

If startup throws errors, paste them straight into Bob's chat. That debugging loop is a genuine part of the experience and worth doing unhurried.

## B · Audit the namespace migration *(~5 min)*

```
Audit all imports for remaining `javax.*` references that should be `jakarta.*`.
```

A good check on whether the recipes were exhaustive — and a good habit to form before trusting any automated migration.

## C · Lab 4 — unit test generation *(~60 min, the best follow-on)*

`Bobathon/labs/lab4-unit-test-generation/LAB4-GUIDE.md`

Bob quotes a cost and duration estimate **with an 80% confidence interval**, generates tests through parallel subagents with isolated contexts, self-corrects mid-run when it hits a compile race — without asking — and reports coverage at the end. It typically finishes well under its own estimate.

> 💡 To run it quickly, set **Candidate Selection Strategy** to a single package (`com.pharmacy.repository`, three classes) rather than **All Classes**. You see the whole mechanism in a fraction of the time. Use All Classes when you have an afternoon.

## D · Lab 5 — security vulnerability remediation *(~60 min)*

`Bobathon/labs/lab5-security-vulnerability-remediation/LAB5-GUIDE.md`

Picks up where this lab's CVE scan left off: SQL injection, XSS, input validation and sanitisation.

## E · Lab 3 — Struts to a modern SPA *(~90 min)*

`Bobathon/labs/lab3-ui-modernization/LAB3-GUIDE.md`

The biggest of the set: Bob converts the Struts backend to Jakarta EE REST services, proves it by serving live JSON, then builds a modern frontend against it.

> ⚠️ Two warnings. The guide needs **Node.js/npm and Docker**, which it does not list in its prerequisites. And the repo README describes this lab as Struts → **Angular** while the guide itself selects **React + Material UI** — expect React.

## F · Lab 1 — WebSphere to Liberty *(~60 min)*

`Bobathon/labs/lab1-java-liberty-replatforming/LAB1-GUIDE.md`

The replatforming story, and the logical predecessor to this lab. Requires Java 8 — already your default on this image.

> ⚠️ In Bob 2.2.1 the Liberty option is labelled **Liberty Modernization**, and it is greyed out unless the project is still on traditional WebSphere — open the `snapA-java-liberty-replatforming` folder, not `snapB`.

---

# Appendix · Troubleshooting

| Symptom | What to do |
|---|---|
| No **Java Modernization** workflow in the list | Premium entitlement missing on your account, or you opened the parent folder instead of `snapB-java-upgrade` |
| Baseline build is very slow | `~/.m2` is not warm on this image — tell your track lead |
| `mvn` reports a different JDK than `java -version` | Maven uses `JAVA_HOME`, which can differ from the shell default. Normal at this stage; Bob manages it during the upgrade |
| Build still fails after the recipes | Paste the full error into Bob's chat. Working the error loop in conversation is part of the lab |
| Bob wants to change something you do not understand | Choose *"show me the exact edits before applying them"*, and ask it why in chat before approving |
| Port 9081 already in use *(optional extra A)* | `./stop-liberty.sh` in the lab directory |
| *"SDKMAN requires Bash 4 or higher"*, or Bob shows *"Failed to install SDKMan: true"* | macOS ships Bash 3.2. Run `brew install bash`, open a new terminal (or restart Bob), then click **Retry** in the workflow |
| `sdk install java 8.0.492-zulu` → *not a valid candidate version* | That build was withdrawn. `sdk list java \| grep zulu` and install the newest `8.0.x-zulu` (8.0.504+1-zulu on 2 Oct 2026) |
| Bob never offered to install Java 21 | A JDK 21 was already installed, so Bob used it. Expected — carry on |
| **Agent steps finish instantly**, *"Request Failed — An unexpected error occurred"*, Usage stays at 0.0000, nothing changes in `pom.xml` | Bob's model calls are being refused. The log (`~/Library/Application Support/IBM Bob/logs/<latest>/window*/exthost/IBM.bob-code/IBM Bob.log`) shows `ProviderError … Caused by: Forbidden` and `ModelInfoError`. Check the team selected in ⚙ Settings → General, sign out and back in, and confirm the team's plan hasn't lapsed. Fix this before continuing — the deterministic steps (recipes, builds, scans) still run, which hides the problem |
| Pop-ups on first open (*Install GitHub Copilot modernization extension*, C++ IntelliSense, "open the parent git repository?") | Not part of the lab. Choose **Not Now** / **Never** |

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

```bash
# SDKMAN, Java 8 (the lab's starting point) and Maven
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 8.0.492-zulu      # Zulu: Temurin 8 is not available on Apple Silicon
sdk default java 8.0.492-zulu
sdk install maven

# The lab code, with Maven's cache warmed so the first build isn't a cold download
git clone https://github.com/vandepol/IBMBobWorkshop
cd IBMBobWorkshop/labs/track2-java-modernization/Bobathon/labs/lab2-java-upgrade/snapB-java-upgrade
mvn -B dependency:go-offline && mvn -B clean
```

Don't install Java 21 yourself: Bob offers to install it during the lab, and that is one of the moments worth seeing. Then **fully quit and restart Bob** so it picks up the SDKMAN tools. Your Bob account needs the **Premium Package for Java** entitlement.

---

## 0:00–0:05 · Smoke test

Everything is pre-installed. This catches a bad image while there is still time.

```bash
java -version    # 1.8.0_492, Zulu — Java 8 is the STARTING state
mvn -version     # 3.6+
```

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
2. Select **Java Modernization**
3. Click **▶ Start**

Notice what this is not: you did not describe your task in a sentence and hope. You launched a defined process with phases, inputs and a summary at the end. That distinction is most of the enterprise argument for the premium package.

---

## 0:09–0:17 · Analyze

**Analyze Java Project**
- The **Project Path** should auto-populate to the `snapB-java-upgrade` folder. Confirm it.
- Leave **Custom build command** blank.
- Click **Continue**.

Bob now scans the project, detects Java 8, and **runs a baseline build**. It is establishing that the application compiles *before* it changes anything — so that if something breaks later, there is no argument about whether it was already broken.

> ⏱️ On the sandbox this should finish in under three minutes; `~/.m2` is pre-warmed. Considerably longer means the warm cache did not make it into the image — tell your track lead.

While it builds, open `pom.xml` and find the `maven.compiler.source` and `target` properties. Java 8. Remember what they say.

---

## 0:17–0:20 · Choose what kind of modernization

**Select Modernization Type**
- Modernization Type: **Java Upgrade**
- Git Flow: **off** *(branch management is outside the scope of this lab)*
- Click **Continue**

Note the other options on this screen — Liberty Replatforming and UI Modernization are separate labs in the same repo, both available to you afterwards.

---

## 0:20–0:40 · Configure the upgrade and run the recipes

This is the longest block in the lab and the one with the most to watch.

**Java Upgrade Configuration**
- Java Distribution: **Semeru (IBM)**
- Target Java Version: **21**
- Jakarta EE Migration: **on**, target **Jakarta EE 10**
- Click **Run Recipes**

### The first thing to watch for

If Java 21 Semeru is not installed, Bob tells you so and offers an **Install** button. Click it.

**Watch what happens.** Bob installs a JDK via SDKMAN, on its own, and then resumes the workflow where it left off. It did not fail and hand you a task. It did not tell you to go and read an installation guide. It noticed a missing prerequisite in its own environment and fixed it.

Stop and register that, because it reframes what this tool is. Everything else in this lab is code transformation; this is environment management.

### Then the recipes run

Bob applies OpenRewrite recipes across the codebase and begins an agentic build pass. You will see:
- `javax.*` imports rewritten to `jakarta.*`
- Dependencies updated for Jakarta EE 10
- Compiler configuration moved to 21
- Repeated compile attempts as it works through fallout

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

**Read the rationale before you approve.** This is the moment the lab is built around. A find-and-replace tool gives you a broken build and an error log. This gave you a diagnosis and a named fix, and then waited for a human to agree.

Where Bob offers *"show me the exact edits before applying them"*, take it at least once. Seeing the diff before it touches your `pom.xml` is the answer to half the questions your architecture review board will ask.

### The CVE scan

Somewhere in this phase Bob runs a **security scan** and asks whether you want the vulnerabilities it found remediated. Say yes.

Consider what just happened: a version upgrade turned into a vulnerability remediation without you scoping it as one. For most institutions those are two separate programmes of work with two separate business cases.

---

## 0:48–0:54 · Green build, and what it cost

**Final Build Verification** — Bob runs `mvn clean compile` under Java 21. You are looking for:

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
| Changes | Committed |
| **Cost** | Typically **3–5 Bob coins**, with a per-task breakdown |

That last row is the one to linger on. The tool is telling you what the work cost, itemised by subtask. Whatever you think of the number, an AI vendor putting a per-task meter in front of you is not the norm, and it is what makes a real business case arithmetically possible rather than a matter of faith.

### ✋ CHECKPOINT 3 — *everyone has BUILD SUCCESS and a summary on screen*

---

## 0:54–1:00 · Read what actually changed, then debrief

Open a file Bob touched:

```
src/main/java/com/pharmacy/action/
```

Look at the imports. `javax.servlet.*` is now `jakarta.servlet.*`. Open `pom.xml` and find the `<dependencyManagement>` block Bob added, and the compiler properties now reading 21.

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
http://localhost:9081/simple-pharmacy/dashboard
```

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

> ⚠️ The guide's file paths say `Bobathon/ce-labs/…`; the actual directory is `Bobathon/labs/…`. Adjust as you read.

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

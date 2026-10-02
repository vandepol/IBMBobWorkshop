# IBM Bob AI Copilot - Java Vulnerabilities Detection Lab Guide (V2)
## Simple Pharmacy Dashboard - Dependency CVE Scanning & Remediation

---

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [V2 Feature Highlights](#v2-feature-highlights)
4. [Setting Up](#setting-up)
5. [Exercise 1: Run the Vulnerabilities Detection Workflow](#exercise-1-run-the-vulnerabilities-detection-workflow)
6. [Exercise 2: Review the CVE Findings](#exercise-2-review-the-cve-findings)
7. [Exercise 3: Remediate the Detected Vulnerabilities](#exercise-3-remediate-the-detected-vulnerabilities)
8. [Exercise 4: Verify the Remediated Application](#exercise-4-verify-the-remediated-application)
9. [Troubleshooting](#troubleshooting)
10. [Conclusion](#conclusion)

---

# Introduction

### What is Dependency Vulnerability Detection?

Every Java application depends on third-party libraries — directly (declared in `pom.xml`) or transitively (pulled in by another library). Any of those libraries can carry known security vulnerabilities cataloged as CVEs. Dependency vulnerability detection is the process of:

- **Scanning** your project's declared and transitive dependencies
- **Matching** them against published CVE databases
- **Reporting** each match with severity, affected versions, and fix recommendations
- **Remediating** by upgrading or excluding the vulnerable dependency

## About This Lab

You'll use **Bob V2's Java Vulnerabilities Detection workflow** to scan the pharmacy application's dependencies, understand each finding, and remediate them by upgrading vulnerable libraries.

- **Before**: pharmacy application with vulnerable dependency versions in `pom.xml`
- **After**: same application with vulnerable dependencies upgraded, clean scan result, and a documented remediation trail

## Learning Objectives

By the end of this lab, you will:
- Launch the Java Vulnerabilities Detection workflow and interpret its scan output
- Understand the difference between direct and transitive dependency vulnerabilities
- Approve dependency upgrades through Bob's interactive remediation flow
- Verify the remediated application still builds cleanly

---

# Prerequisites

### 1. IBM Bob IDE (V2)
- Latest Bob V2 IDE extension installed
- Bob subscription tier that includes the Java Vulnerabilities Detection workflow (the Premium package)

### 2. Terminal Environment (macOS zsh)
If SDKMAN isn't set up:
```bash
curl -s "https://get.sdkman.io" | bash
echo '[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"' >> ~/.zshrc
source ~/.zshrc
```

### 3. Java 21
```bash
sdk list java | grep " 21\."
```
Confirmed working on Apple Silicon: `21.0.11-zulu`.
```bash
sdk install java 21.0.11-zulu
sdk use java 21.0.11-zulu
```

### 4. Maven
```bash
sdk install maven
```

### 5. Restart Bob
Fully quit and restart Bob after installing Maven.

*Note: this lab is backend-only. Node.js and npm are not required.*

---

# V2 Feature Highlights

Worth watching for and demonstrating during this lab:

- **Standalone workflow at the top level**: Java Vulnerabilities Detection is a dedicated top-level workflow — not a sub-type inside Java Modernization.
- **Fast, deterministic scan**: unlike the modernization workflows (which are agentic and iterative), the vulnerabilities scan is a targeted CVE lookup that completes in seconds.
- **Direct + transitive scope**: Bob resolves the full dependency tree, not just what's directly declared in `pom.xml`. Transitive vulnerabilities are reported alongside direct ones.
- **Interactive remediation flow**: for each detected CVE, Bob proposes a specific fix (usually a version bump or dependency swap) and asks for approval before applying.
- **Per-task cost breakdown**: even short workflow runs show the per-task cost and token accounting.

---

# Setting Up

### 1. Open the snapshot subfolder as your project root
```
Bobathon/labs/lab5-security-vulnerability-remediation/snapE-security-vulnerabilities
```
Use the `snapE-*` subfolder, not the parent `lab5-*` folder — the workflow only appears at the snapshot level.

### 2. Confirm Agent mode
Bob's chat panel should show **Agent** at the bottom.

### 3. Confirm the workflow appears
Look for **Java Vulnerabilities Detection** in Bob's chat panel workflow list.

---

# Exercise 1: Run the Vulnerabilities Detection Workflow

### Objective
Trigger the workflow and let Bob perform a full dependency scan.

### Steps

1. **Start the workflow**
   - In Bob's chat panel, click **Start** on the **Java Vulnerabilities Detection** workflow.

   ![Java Vulnerabilities Detection workflow in Bob's workflow panel](images/Workflow_select_image.png)

2. **Dependency detection**
   Bob reads `pom.xml`, resolves the full dependency tree (direct + transitive), and reports the number of packages it will scan.

3. **CVE lookup**
   Bob queries the CVE database for each detected package. This step is fast — typically under 30 seconds for a project of this size.

4. **Findings report**
   Bob reports each detected vulnerability with:
   - Affected package and version
   - CVE identifier(s)
   - Severity (Critical, High, Medium, Low)
   - Whether the dependency is direct or transitive
   - Suggested remediation

---

# Exercise 2: Review the CVE Findings

### Objective
Understand what Bob found and prioritize before remediating.

### Steps

1. **Read through each finding**
   For every CVE, note:
   - Is the vulnerable library directly declared or pulled in transitively?
   - What's the fixed version Bob recommends?
   - What's the severity, and does it apply to how your app actually uses the library?

2. **Group the findings**
   Ask yourself:
   - Which findings are quick fixes (single version bump)?
   - Which require excluding a transitive dependency and adding a direct replacement?
   - Are any duplicates caused by multiple paths bringing in the same vulnerable version?

3. **Confirm you understand each CVE before remediating**
   For unfamiliar CVEs, ask Bob directly in the chat: `Explain CVE-XXXX-XXXX in plain terms and how it could affect this application.` Bob will summarize the vulnerability and its practical impact.

---

# Exercise 3: Remediate the Detected Vulnerabilities

### Objective
Apply Bob's proposed fixes through the interactive remediation flow.

### Steps

1. **Start remediation**
   From the findings report, tell Bob to proceed with remediation. Bob will step through the CVEs one at a time.

   ![Bob prompting to fix 9 detected vulnerabilities](images/Workflow_vulnerability_image.png)

2. **Interactive approval flow**
   For each finding, Bob will:
   - Explain the CVE and its impact
   - Propose a specific `pom.xml` change (version bump, exclusion + direct dependency, `<dependencyManagement>` pin, etc.)
   - Ask for approval before applying

3. **Preview individual edits**
   If you're not sure about a proposed change, choose the option to see the exact `pom.xml` edits before applying. Bob will render the diff for review.

4. **Approve iteratively**
   Work through each CVE in the order Bob presents them. Bob will apply each edit, then move on to the next.

5. **Handle knock-on effects**
   Some fixes trigger secondary issues (e.g. an upgrade to a new major version that requires an API change elsewhere). Bob will identify these and prompt for approval on additional fixes.

---

# Exercise 4: Verify the Remediated Application

### Objective
Confirm the app still builds and re-scan to confirm the CVEs are cleared.

### Steps

1. **Compile check**
   In Bob's terminal:
   ```bash
   mvn clean compile
   ```
   You should see `BUILD SUCCESS`.

2. **Re-run the workflow**
   Click **Start** on the Java Vulnerabilities Detection workflow again. Bob should now report either zero findings or a substantially reduced list.

3. **Optional: run the Liberty server**
   ```bash
   mvn liberty:run
   ```
   Confirm the api comes up cleanly on `http://localhost:9081/simple-pharmacy/api/dashboard` with no runtime errors introduced by the upgrades. It should look something like this:

   ![Pharmacy dashboard API responding with prescription data](images/Workflow_api_image.png)

---

# Troubleshooting

## Issue 1: Workflow reports zero vulnerabilities

**Symptom:** Bob's scan completes with "No vulnerabilities found affecting the detected packages."

**Solution:** This can be a legitimate result (clean dependencies), but for this lab it means `pom.xml` doesn't include the intentionally vulnerable versions the lab expects. Check that you opened the `snapE-*` starting snapshot and haven't modified it.

## Issue 2: `mvn compile` fails after a version bump

**Symptom:** A CVE remediation upgrades a library to a version with a breaking API change, and the code no longer compiles.

**Solution:** Paste the compile error back to Bob and ask it to reconcile the API change. Bob will either adapt the calling code or propose a different (still-secure) version.

## Issue 3: A vulnerable dependency is pulled in transitively and won't upgrade cleanly

**Symptom:** A CVE lives inside a transitive dependency that Bob can't simply version-bump because another library requires the older version.

**Solution:** Bob typically proposes an `<exclusions>` block on the outer dependency plus a direct declaration of the fixed version. Approve when prompted.

## Issue 4: Bob's terminal shows the wrong Java version

**Symptom:** `java -version` in Bob's terminal shows a version other than 21.

**Solution:** `sdk use java 21.0.11-zulu` in Bob's terminal specifically. `sdk use` is shell-scoped and doesn't apply across terminal tabs.

---

# Conclusion

You've completed the Java Vulnerabilities Detection lab using Bob V2's dedicated workflow. You should now be comfortable with:

- ✅ Launching the Java Vulnerabilities Detection workflow
- ✅ Reading a CVE findings report and distinguishing direct vs transitive vulnerabilities
- ✅ Approving dependency changes through the interactive remediation flow
- ✅ Handling knock-on effects (breaking upgrades, transitive exclusions)
- ✅ Verifying the remediated application with a clean re-scan

You've now completed all five Java Modernization labs.

---

# IBM Bob for Z — 1-Hour Workshop

**Total time:** 60 minutes  
**Prerequisites:** Complete the Pre-Work Checklist before the session  
**Sample workspace:** [Bank of Z](https://github.com/IBM/Bank-of-Z) — clone and open in IBM Bob before you begin

---

## Pre-Work Checklist

Complete these steps **before** the workshop session.

1. **Create an IBM ID** — https://www.ibm.com/account/reg/us-en/signup?formid=urx-19776
2. **Download IBM Bob** — https://bob.ibm.com/download
3. **Sign in** using your IBM ID
4. **Install Premium Package for Z** — Settings → Add-ons → Premium Package for Z → Install

   ![Install Premium Package for Z](images/03-addons.png)

5. **Clone the sample workspace**

   ```bash
   git clone https://github.com/IBM/Bank-of-Z
   ```

   Open the `Bank-of-Z` folder in IBM Bob.

**Verify before the session:**

- [ ] IBM Bob installed and signed in
- [ ] Z Code and Z Architect modes visible

  ![Z Code and Z Architect Modes](images/04-modes.png)

- [ ] Bank of Z repository open in IBM Bob

> **Can't install locally?** Request a VM account from the IBM team — access at https://vdi.cloud.techzone.ibm.com/guacamole

---

## Setup Verification

At the start of the session, confirm:

- [ ] IBM Bob is open with the Bank of Z folder loaded
- [ ] **Z Code** and **Z Architect** modes are visible
- [ ] Premium Package for Z is installed (Settings → Add-ons)

> If you hit any issues, flag an IBM team member now.

---

## Exercise 1: Workspace Initialization & Data Dictionary

**Mode:** Z Code

### Exercise 1.1: Generate the AGENTS.md File

1. Open **IBM Bob Chat** and switch to **Z Code mode**.
2. Run:
   ```
   /init
   ```
3. Bob scans the workspace, detects languages and copybook paths, and creates `AGENTS.md` in the workspace root.
4. Open `AGENTS.md` and verify it was created.

> ⚠️ Z Understand server is not set up for this workshop — local analysis only.

---

> 🆕 **New Task:** Create a new task in IBM Bob before Exercise 1.2.

### Exercise 1.2: Build a Data Dictionary

**Option A — Chat prompt:**
```
Create a data dictionary for INQACC.cbl
```

**Option B — Workflow button:**
1. Open `INQACC.cbl` in the editor.
2. Click **Workflows** → **Generate data dictionary**.

![Generate Data Dictionary Workflow](images/05-datadict-workflow.png)

Bob scans the program, generates short and long descriptions for each variable, and saves the result to `.bobz/DD.json`. Bob references this file automatically in all future interactions.

**More prompts:**
```
Add variables from CREACC.cbl to the data dictionary
```
```
Which programs use the variable HV-ACCOUNT-ACC-NO?
```

---

## Exercise 2: Impact Analysis & Call Chain Tracing

**Mode:** Z Architect

### Exercise 2.1: Generate an Impact Analysis

1. Switch to **Z Architect mode**.
2. Request an impact analysis:
   ```
   Analyze the impact of adding a new ACCOUNT-CREDIT-LIMIT field to the ACCOUNT.cpy copybook
   ```
3. Bob analyses across four levels — Code, Application, System, and Operation — and saves the report to `.bobz/impact-analysis/`.
4. Open and review `IMPACT-ANALYSIS.md` — it includes mermaid dependency diagrams, risk ratings, and a testing plan.

**More prompts:**
```
What will be affected if I modify the SORTCODE.cpy copybook?
```
```
Create an implementation plan for adding overdraft alert notifications to INQACC.cbl
```

---

> 🆕 **New Task:** Create a new task in IBM Bob before Exercise 2.2.

### Exercise 2.2: Cross-Program Call Chain Tracing

1. Still in **Z Architect mode**, ask:
   ```
   Which program in this repo calls XFRFUN, and how does the data get passed between them?
   ```
2. Bob identifies the caller, maps the COMMAREA layout, and traces the full chain without you opening a single file.

**More prompts:**
```
What programs does DBCRFUN call or link to?
```
```
Show me the full call chain from BNKMENU down to the DB2 account update
```

---

## Exercise 3: Code Understanding & Documentation

**Mode:** Z Code

### Exercise 3.1: Generate a Code Explanation

**Option A — Chat prompt:**
```
Explain what @INQACC.cbl does
```
```
Explain @DBCRFUN.cbl in simple terms for a new developer
```

**Option B — Workflow button:**
1. Open the file in the editor.
2. Click **Workflows** → **Explain code**.
3. Choose a perspective: **Architect**, **Business**, or **Developer**.

Bob resolves all copybooks automatically and references the data dictionary for variable context.

---

> 🆕 **New Task:** Create a new task in IBM Bob before Exercise 3.2.

### Exercise 3.2: Generate Program Documentation

**Option A — Chat prompt:**
```
Generate documentation for @XFRFUN.cbl
```

Output is saved to `docs/program-documents/[program-name].md`.

**Option B — Workflow button:**
1. Click **Workflows** → **Generate documentation**.
2. Choose the target program and perspective (Architect, Developer, or Business).

**More prompts:**
```
Create comprehensive documentation for @DBCRFUN.cbl
```
```
Document all CICS COBOL programs in src/base/cics/cobol/
```

---

## Exercise 4: Code Generation

**Mode:** Z Code

### Exercise 4.1: Generate a New COBOL Program

1. Switch to **Z Code mode**.
2. Describe the requirement:
   ```
   Generate a COBOL CICS program that validates a customer credit score against the DB2 CUSTOMER table and returns eligibility status
   ```
3. Bob generates a complete program following Bank of Z conventions — `CBL CICS(...)`, `HV-` host variable prefix, `TEST-` condition names, `COPY SORTCODE`, and SQLCODE checks after every `EXEC SQL`.
4. Review the diff and approve the change.

**More prompts:**
```
Generate a new CICS COBOL program that retrieves customer details from the DB2 CUSTOMER table
```
```
Add a new paragraph to @INQACC.cbl that sends an IBM MQ message when the available balance drops below the overdraft limit
```
```
Update @XFRFUN.cbl to handle zero-amount transfer attempts with a CICS ABEND
```

---

## 🎉 Workshop Complete!

| Exercise | Capability |
|----------|-----------|
| Exercise 1 | Workspace initialization, AGENTS.md, data dictionary |
| Exercise 2 | Impact analysis, call chain tracing |
| Exercise 3 | Code explanation, program documentation |
| Exercise 4 | COBOL code generation |

**IBM Bob Documentation:** https://bob.ibm.com/docs/ide

---

## Appendix: Optional Deep Dives

Explore these after the session at your own pace.

---

### Appendix A: Honest Gap Handling

In **Z Architect mode**, ask:
```
CRDTAGY1.cbl calls external credit agency programs via the CICS Async API. Are those programs available in this repository?
```
Bob states clearly when a program is not in the repository — it does not fabricate behaviour.

---

### Appendix B: End-to-End Data Lineage

In **Z Architect mode**, ask:
```
Trace ACCOUNT_ACTUAL_BALANCE from the DB2 table definition through every program that reads or updates it.
```

---

### Appendix C: COMP and COMP-3 Storage

In **Z Code mode**, open `INQACC.cbl` and ask:
```
In INQACC.cbl, explain HV-ACCOUNT-AVAIL-BAL, HV-ACCOUNT-ACTUAL-BAL, and HV-ACCOUNT-OVERDRAFT-LIM. How are they stored in memory and why were these types chosen for a banking app?
```

---

### Appendix D: REDEFINES Clause

In **Z Code mode**, ask:
```
In ACCOUNT.cpy, explain the REDEFINES on ACCOUNT-OPENED. How does it work and when would each view be used?
```

---

### Appendix E: CICS and MQ Transaction Flow

In **Z Code mode**, ask:
```
Walk me through what DBCRFUN does when a teller processes a cash deposit — from receiving the COMMAREA to updating the database.
```

---

### Appendix F: Modifying Existing Code

In **Z Code mode**, open the target file and ask:
```
Modify @INQACC.cbl to return the ACCOUNT-OVERDRAFT-LIMIT in addition to the balance fields
```
```
Add PROCTRAN audit logging to @DBCRFUN.cbl for all debit/credit transactions
```

---

*IBM Bob for Z Workshop — Bank of Z sample application*

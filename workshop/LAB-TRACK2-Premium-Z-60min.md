# Track 2 — IBM Bob, Premium Package for Z
## Lab: inheriting a 40-year-old CICS/IMS/DB2 bank on your first morning

**Time:** 60 minutes
**Environment:** pre-baked sandbox VM — nothing to install. Or your own laptop — see *Running on your own laptop* below.
**Bob tier:** Premium Package for Z
**Codebase:** Bank of Z (`github.com/IBM/Bank-of-Z`) — CICS and IMS COBOL, PL/I batch, DB2

> Uses the Bank of Z codebase in `labs/track2-z-bank-of-z`.

---

## What you are about to do

You have joined a team that owns a core banking platform written before most of the room started school. Nobody who wrote it is still here. There is no current documentation. The variable names are six characters long and mean something to someone who retired in 2011.

In the next hour you will onboard onto that codebase, understand it, work out the blast radius of a change, and then make one — with Bob.

The thing to watch for is not whether it writes COBOL. It is whether it tells you the truth about code it cannot see.

**By the end you will have seen:**
- Bob **indexing an unfamiliar mainframe codebase** and writing its own briefing note
- A **data dictionary** built from undocumented host variables
- Bob explaining not just *what* a `COMP-3` field is but *why* it was the right choice
- **Cross-program call chains** and **DB2 field lineage** traced through CICS, IMS and PL/I
- Bob **declining to guess** about a program that isn't in the repository
- A change proposed as a **diff**, with a human approval gate

---

## Running on your own laptop

Do this **before** the session. On the sandbox VM it has already been done.

1. In Bob: ⚙ → **Add-ons** → install **Premium Package for Z**. It needs Bob 1.0.1+ and **IBM Z Open Editor 6.4.0+**. Remove any **IBM watsonx Code Assistant for Z** extension first, or the add-on will not activate. Your Bob account needs the Premium Package for Z entitlement.
2. Get the code:
   ```bash
   git clone https://github.com/vandepol/IBMBobWorkshop
   ```
3. Open `IBMBobWorkshop/labs/track2-z-bank-of-z` in Bob at that folder (the Bank of Z repository root). No mainframe, container or Z Understand server is needed; everything runs against the local workspace.
4. Optional, but saves 10–15 minutes on the day: ask Bob once *"Which program in this repo calls XFRFUN, and how does the data get passed between them?"* so its project index is built. Do **not** run `/init` ahead of time; that is the first exercise.

---

## 0:00–0:04 · Smoke test

Everything is pre-installed. This catches a bad image early.

In Bob:
- Bob Chat panel opens and you are signed in
- The mode selector shows **Z Code** and **Z Architect**
- The workspace is open at the **Bank of Z repository root**, and you can see:

```
src/base/cics/cobol/     src/base/cics/copy/
src/base/ims/cobol/      src/base/ims/copy/
src/base/batch/pli/      src/base/batch/jcl/
```

> 🚩 **No Z Code / Z Architect modes?** The Premium Package for Z add-on is not installed or your entitlement is missing. Raise your hand now. Try **View → Command Palette → Developer: Reload Window** first.

> ℹ️ **Z Understand is not configured for this workshop.** Everything today runs against local workspace analysis. If a prompt offers you a choice of analysis source, choose **Local Workspace Analysis**.

### ✋ CHECKPOINT 1 — *everyone has both Z modes and the Bank of Z tree*

---

## 0:04–0:07 · The setup

Before you touch anything, spend two minutes in the code. Open `src/base/cics/cobol/INQACC.cbl` and just look at it.

Then find `HV-ACCOUNT-AVAIL-BAL` in it and ask yourself, honestly: without asking a colleague, could you say what that field means, what it is stored as, and which other programs write to it?

That gap is what this hour is about.

---

## 0:07–0:19 · Block 1 — Onboard

### Let Bob read the estate

Switch to **Z Code** mode. In a new chat, type:

```
/init
```

Bob scans the workspace, detects the languages present, locates the copybook directories, and writes an **`AGENTS.md`** at the workspace root.

> ⏱️ 2–5 minutes on Bank of Z. While it runs, keep `INQACC.cbl` open.

When it finishes, **open `AGENTS.md` and read it.** This is Bob's own briefing note on your estate — structure, languages, where copybooks live, conventions it spotted. It is also a file you can edit, review and commit. Everything Bob does from here is grounded in it.

> 💡 That is the governance answer in one artefact: the context the model works from is a text file in your repo, not something hidden in a vendor's service.

### Build a data dictionary

Still in **Z Code** mode. Open `INQACC.cbl`, then use the **Workflows** button in the Bob Chat panel → **Generate data dictionary**.

*(You can also just ask, if you prefer the chat route:)*

```
Create a data dictionary for INQACC.cbl
```

Bob resolves the `COPY` statements and `EXEC SQL INCLUDE` copybooks, extracts the PIC clauses, and writes short and long descriptions for each variable into `.bobz/DD.json`.

Open `.bobz/DD.json` and find `HV-ACCOUNT-AVAIL-BAL`. Look at:
- the **type** it inferred, and what it says about packed decimal
- the **business meaning** — note whether it worked out that this is net of holds
- the **`scope`** array — which programs use this variable

Bob derived business meaning and cross-program usage from undocumented host variables. That is the tribal knowledge your team currently keeps in people's heads.

```
Show me the data dictionary entry for HV-ACCOUNT-AVAIL-BAL
```

> 💡 Entries are marked `origin: "AI"`. You can edit any description by hand and it becomes authoritative. This is a living asset your team owns, not a one-off report.

### ✋ CHECKPOINT 2 — *everyone has `AGENTS.md` and a populated `.bobz/DD.json`*

---

## 0:19–0:34 · Block 2 — Understand

### A straight explanation

Still in **Z Code** mode:

```
Explain what @INQACC.cbl does
```

Note that Bob resolved `ACCOUNT.cpy`, `ACCDB2.cpy` and `SORTCODE.cpy` without you opening any of them, and that it is using the data dictionary you just built for variable context.

### The same program for a different audience

Open `src/base/cics/cobol/DBCRFUN.cbl`. Click **Workflows** → **Explain code** → choose the **Business** perspective.

Then compare it against what the **Developer** perspective gives you.

Same code, two documents, two audiences. The version your BA can read is the one that usually does not exist.

### The question a COBOL developer will actually judge it on

```
In INQACC.cbl, explain HV-ACCOUNT-AVAIL-BAL, HV-ACCOUNT-ACTUAL-BAL, and HV-ACCOUNT-OVERDRAFT-LIM. How are they stored in memory and why were these types chosen for a banking app?
```

**Read this answer carefully.** You are looking for whether it explains that `S9(10)V99 COMP-3` is packed decimal, that it occupies 7 bytes of BCD, that it maps to DB2 `DECIMAL(10,2)` — and critically, *why binary floating point would be wrong for money*, because 0.10 has no exact IEEE 754 representation and cents drift over millions of transactions.

That is not pattern matching. That is the reasoning a senior mainframe developer applies, and it is the answer that decides whether the room takes this seriously.

### Copybook resolution

```
XFRFUN.cbl COPYs several copybooks. What does each one contribute and what does the LINKAGE SECTION look like when fully expanded?
```

You never opened a copybook. It still produced the fully expanded LINKAGE SECTION.

> ⏱️ **Running behind?** Skip the copybook prompt. The `COMP-3` answer above is the one that matters.

---

## 0:34–0:51 · Block 3 — Trace and assess

Switch to **Z Architect** mode.

### Start the long-running one first

Kick this off now so it generates while you work through the rest of the block:

```
Analyze the impact of adding a new ACCOUNT-CREDIT-LIMIT field to the ACCOUNT.cpy copybook
```

Leave it running. You will come back to it. *(If offered an analysis source, choose **Local Workspace Analysis**.)*

### Who calls what

In a second chat:

```
Which program in this repo calls XFRFUN, and how does the data get passed between them?
```

Bob identifies `BNK1TFN`, the `EXEC CICS LINK`, and describes the `BNK1TFNC` COMMAREA field by field — `COMM-SORTCODE`, `COMM-ACCNO`, `COMM-ACC-ACCNO`, `COMM-AMT`.

Consider how long that takes by hand across a CICS estate, and how confident you would be that you found every caller.

### 🎯 The most important prompt in this lab

```
CRDTAGY1.cbl calls external credit agency programs via the CICS Async API. Are those programs available in this repository?
```

**Stop and read the answer properly.**

Bob checks the inventory and tells you those sub-programs are **not in this repository**. It then confines itself to what it can legitimately infer from the visible channel and container interface — and it does not invent the rest.

Everyone in your organisation who is nervous about this technology is nervous about exactly one thing: that it will confidently make something up about code it cannot see, and someone will believe it. This is the prompt that tests it. Bring the answer to the regroup.

### Field lineage, end to end

```
Trace ACCOUNT_ACTUAL_BALANCE from the DB2 table definition through every program that reads or updates it.
```

Bob chains its project-wide tools and produces a lineage table down to the paragraph: `DBCRFUN 1400-UPDATE-ACCOUNT`, `XFRFUN 1500-UPD-BOTH-ACCOUNTS`, `INQACC 1000-GET-ACCOUNT`, `INQACCCU 1100-GET-ACCOUNT`.

Look at that table as someone answering a regulator's question about a balance figure. Paragraph-level traceability of a monetary field across a CICS/IMS estate, produced in about three minutes, is a compliance artefact as much as an engineering one.

### Now go back to the impact analysis

Open `.bobz/impact-analysis/[change-name]-[timestamp]/IMPACT-ANALYSIS.md`.

**Do not read it all.** Skim three things:
1. The **executive summary** — impact scope and risk level
2. The **mermaid change-propagation diagram**
3. The **Operation-level** section — build sequencing, JCL `LRECL` changes, data migration, and the **rollback plan**

The operation-level section is the credibility moment for a bank. An AI that volunteers "recompile in this order, update the JCL, support both record formats during transition, and here is how you back it out" is reasoning about production, not just about source.

### ✋ CHECKPOINT 3 — *everyone has seen the honest-gap answer and the lineage table*

---

## 0:51–0:58 · Block 4 — Change something

Switch back to **Z Code** mode. Open `INQACC.cbl`.

```
Modify @INQACC.cbl to return the ACCOUNT-OVERDRAFT-LIMIT in addition to the balance fields
```

**Do not approve it yet.** Read the diff first and check:

- Did it use the `HV-` host-variable prefix the rest of the program uses?
- Did it keep the existing `EXEC SQL` shape rather than rewriting it?
- Did it add a **SQLCODE check** after the SQL, like every other statement in the file?
- Did it preserve the CICS flow and the `TEST-` 88-level condition names?

Nobody told Bob those conventions. It inferred them from the codebase and from `AGENTS.md`.

Then approve it — and note that **nothing was written to disk until you clicked**.

> 💡 If there is time, ask for something built from scratch and check the same conventions hold:
> ```
> Generate a COBOL CICS program that validates a customer credit score against the DB2 CUSTOMER table and returns eligibility status
> ```
> Look for `EXEC CICS RETURN` rather than `STOP RUN` — the CICS task-lifecycle detail it was never told about.

---

## 0:58–1:00 · Wrap

Look at what exists on disk that did not an hour ago:

```
AGENTS.md                                    ← the estate, described
.bobz/DD.json                                ← your variables, documented
.bobz/impact-analysis/…/IMPACT-ANALYSIS.md   ← blast radius, with a rollback plan
docs/program-documents/…                     ← program specs
+ an approved, convention-correct diff
```

**Two minutes before the regroup:**
- Did it lie to you once? *(The `CRDTAGY1` answer is the one to judge on.)*
- How long would that field lineage have taken your team?
- Which of these artefacts would you actually keep and maintain?
- Where do you want a human gate that the tool did not give you one?

Bring one surprise and one criticism. The criticism is the more useful of the two.

---

# Optional extras

Take these home. The sandbox is yours to keep.

## A · Refactor, then transform to Java *(~45 min — the natural next step)*

`Lab-5-Code-Modernization.md`

Use `/refactor` on `DBCRFUN.cbl` to extract a calculation paragraph into a standalone module, then `/transform` the result into Java and run `mvn test`.

> ⚠️ Cut from the live session deliberately: `/refactor` is a four-gate interactive flow and a room of twenty ends up on four different gates. It works much better at your own pace.
> ⚠️ Needs a **JDK and Maven** — undeclared in the guide. Confirm your sandbox has them before starting.

## B · Validate the transformation *(~30 min)*

`Lab-5-Code-Generation-Validation.md` Exercise 5.2 — `/validate` compares COBOL against generated Java and writes a report to `.bobz/validation-reports/`.

> Point `/validate` at the COBOL program you transformed and the Java your `/transform` actually generated, e.g. `@<PROGRAM>.cbl @<Program>.java`.

## C · Document the whole CICS estate *(~20 min, run it and walk away)*

```
Document all CICS COBOL programs in src/base/cics/cobol/
```

Output lands in `docs/program-documents/`. A genuinely useful artefact to bring back to your team.

## D · Build an implementation plan *(~15 min)*

The Bank of Z track lost this exercise in a merge; the prompts survive:

```
Create an implementation plan for adding overdraft alert notifications to INQACC.cbl
```
```
Plan this change: Add multi-currency support to XFRFUN.cbl fund transfer processing
```

Output: `.bobz/implementation-plans/…/implementation-plan.md`.

## E · More tracing

```
Which programs write to the PROCTRAN table and in which paragraph?
```
```
Show me the full call chain from BNKMENU down to the DB2 account update
```
```
Explain the CICS Async API pattern used in CRDTAGY1.cbl — what are channels and containers and how do they replace COMMAREAs?
```
```
In ACCOUNT.cpy, explain the REDEFINES on ACCOUNT-OPENED. How does it work and when would each view be used?
```

---

# Appendix · Troubleshooting

| Symptom | What to do |
|---|---|
| Z Code / Z Architect modes missing | Premium Package for Z not installed or not entitled. **View → Command Palette → Developer: Reload Window**, then raise your hand |
| Copybooks not resolving | Check the `zopeneditor.copybook` setting points at `src/base/cics/copy/` and `src/base/ims/copy/` |
| A prompt asks for an analysis source | Choose **Local Workspace Analysis**. Z Understand is not configured for this workshop |
| `/init` is taking a long time | Normal on a first scan of this repo — 2–5 minutes. If beyond 8, tell your track lead |
| Project-wide queries return nothing useful | The project index may not be warm on your image. Tell your track lead — this should have been baked in |
| Bob proposed a change you do not agree with | Decline it and ask why. That conversation is worth more than the code |
| You see `<<<<<<< HEAD` in a lab document | You are reading the raw repo rather than this guide. Use this guide — the repo files have unresolved merge conflicts |

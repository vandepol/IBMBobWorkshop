# Injected Vulnerabilities

This document describes the security vulnerabilities intentionally introduced into the
snapE pharmacy application. They serve as a realistic starting point for the
**Java Vulnerabilities Detection** workflow, which scans dependencies against
[osv.dev](https://osv.dev) and applies targeted version-bump fixes.

---

## Summary

| # | Dependency | Vulnerable Version | CVE / GHSA | Severity | Fix Version |
|---|------------|--------------------|------------|----------|-------------|
| 1 | `commons-text` | `1.9` | CVE-2022-42889 | **CRITICAL** | `1.10.0` |
| 2 | `snakeyaml` | `1.30` | GHSA-mjmj-j48q-9wg2 | **HIGH** | `2.0` |
| 3 | `commons-lang3` | `3.11` | GHSA-j288-q9x7-2f5v | **HIGH** | `3.18.0` |

---

## Why These Three

Four vulnerabilities were originally planned (log4j, jackson-databind, commons-text,
snakeyaml). Log4j and jackson-databind were removed after testing revealed that OSV
tracks **future-versioned CVEs** against those packages whose fix versions do not yet
exist on Maven Central:

| Removed Dependency | Unresolvable CVEs | Required Fix (not yet released) |
|--------------------|-------------------|----------------------------------|
| `log4j-core` | GHSA-3pxv-7cmr-fjr4, GHSA-6hg6-v5c8-fphq, GHSA-445c-vh5m-36rj, GHSA-vc5p-v9hr-52mj | `2.25.4` |
| `jackson-databind` | GHSA-hgj6-7826-r7m5, GHSA-j3rv-43j4-c7qm, GHSA-rmj7-2vxq-3g9f | `2.18.8` |
| `jackson-core` | GHSA-72hv-8253-57qq | `2.18.6` |

`commons-lang3` is declared explicitly (it is otherwise a transitive dependency of
`commons-text:1.9` at version `3.11`) so the workflow can detect and pin it to `3.18.0`.

---

## Vulnerability Details

### 1. Text4Shell — CVE-2022-42889 (CRITICAL)

- **Dependency:** `org.apache.commons:commons-text`
- **Vulnerable version:** `1.9`
- **Fix version:** `1.10.0`
- **OSV entry:** https://osv.dev/vulnerability/GHSA-599f-7c49-w659
- **CVSS:** 9.8

**Description:** `StringSubstitutor` in commons-text supports interpolation prefixes
including `${script:...}`, `${url:...}`, and `${dns:...}` in addition to plain variable
substitution. If any part of the substitution map contains attacker-controlled input, this
allows arbitrary script execution, DNS exfiltration, or URL fetching.

**How it was injected:**
- Added `commons-text:1.9` to `pom.xml`
- Created `src/main/java/com/pharmacy/util/LabelTemplateService.java` which uses
  `StringSubstitutor` to render medicine dispense labels from a template
- `LabelTemplateService` is instantiated as a field in `MedicineResource`

**To remove if needed:** Delete `LabelTemplateService.java`, remove its two field
references (`formularyLoader`, `labelService`) from `MedicineResource.java` along with
the formulary header block in `getMedicineById`, and remove `commons-text` from `pom.xml`.

---

### 2. SnakeYAML Constructor Deserialization RCE — GHSA-mjmj-j48q-9wg2 (HIGH)

- **Dependency:** `org.yaml:snakeyaml`
- **Vulnerable version:** `1.30`
- **Fix version:** `2.0`
- **OSV entry:** https://osv.dev/vulnerability/GHSA-mjmj-j48q-9wg2

**Description:** The default `new Yaml()` constructor allows YAML that references
arbitrary Java classes (e.g., `!!java.net.URL`). Loading attacker-controlled YAML results
in arbitrary Java class instantiation and potential remote code execution.

**How it was injected:**
- Added `snakeyaml:1.30` to `pom.xml`
- Created `src/main/java/com/pharmacy/util/FormularyLoader.java` which uses `new Yaml()`
  to load `src/main/resources/formulary.yaml`, returning approved medicine categories
- `FormularyLoader` is instantiated in `MedicineResource` and called during `getMedicineById`
  to add an `X-Formulary-Approved` response header

**To remove if needed:** Delete `FormularyLoader.java` and `src/main/resources/formulary.yaml`,
remove the `FormularyLoader` field and formulary header block from `MedicineResource.java`,
and remove `snakeyaml` from `pom.xml`.

---

### 3. Commons Lang3 Uncontrolled Recursion — GHSA-j288-q9x7-2f5v (HIGH)

- **Dependency:** `org.apache.commons:commons-lang3`
- **Vulnerable version:** `3.11`
- **Fix version:** `3.18.0`
- **OSV entry:** https://osv.dev/vulnerability/GHSA-j288-q9x7-2f5v

**Description:** Uncontrolled recursion in `commons-lang3` can cause a stack overflow
denial-of-service when processing certain inputs.

**How it was injected:**
- `commons-text:1.9` pulls in `commons-lang3:3.11` as a transitive dependency
- Declared explicitly in `pom.xml` at the transitive version (`3.11`) so the workflow
  can detect and pin it to the fix version (`3.18.0`)
- No dedicated utility class — the transitive dependency alone is sufficient for detection

**To remove if needed:** Remove the `commons-lang3` entry from `pom.xml`.

---

## Files Changed

### Modified
| File | Change |
|------|--------|
| `pom.xml` | Added `commons-text:1.9`, `snakeyaml:1.30`, `commons-lang3:3.11` |
| `src/main/java/com/pharmacy/api/MedicineResource.java` | Added `FormularyLoader` + `LabelTemplateService` fields; formulary header on `getMedicineById` |

### Added
| File | Purpose |
|------|---------|
| `src/main/java/com/pharmacy/util/LabelTemplateService.java` | Label renderer using commons-text `StringSubstitutor` |
| `src/main/java/com/pharmacy/util/FormularyLoader.java` | YAML config loader using snakeyaml |
| `src/main/resources/formulary.yaml` | Formulary config consumed by `FormularyLoader` |

---

## Expected Workflow Output

When the **Java Vulnerabilities Detection** workflow runs against this project it should:

1. Detect all 3 vulnerable packages (snakeyaml surfaces multiple CVEs)
2. Report at minimum: CVE-2022-42889 (Text4Shell), GHSA-mjmj-j48q-9wg2 (SnakeYAML RCE), GHSA-j288-q9x7-2f5v (commons-lang3)
3. Apply the following version bumps to `pom.xml`:

| Dependency | Before | After |
|------------|--------|-------|
| `commons-text` | `1.9` | `1.10.0` |
| `snakeyaml` | `1.30` | `2.0` |
| `commons-lang3` | `3.11` | `3.18.0` |

All fix versions are confirmed available on Maven Central and resolve all detected CVEs.

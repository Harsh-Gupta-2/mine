# The Java Developer Interview Handbook — Plan (Step 0)

Date started: 2026-10-06
Workspace: `Java Interview Handbook/` (was empty; this is a greenfield build)
Master prompt: [docs/qb/PROMPT.md](docs/qb/PROMPT.md) (saved verbatim)

## 1. Tool check (what this agent can actually do here)

| Capability | Status | Detail |
|---|---|---|
| Open / read web pages | YES | Verified in session by opening a public interview-experience page and reading its text. |
| Web *search* (keyword query) | PARTIAL / UNVERIFIED | No dedicated search tool is available. Searching must be done by navigating a search engine results page in the built-in browser, which may be blocked or rate-limited. **Will be tested at the start of Step 1.** |
| Write files | YES | Full read/write in the workspace. |
| Run commands | YES | Windows PowerShell 5.1, persistent session. |
| Compile + run Java | YES | JDK 21 found at `~/.vscode/extensions/redhat.java-1.56.0-win32-x64/jre/21.0.12.1-win32-x86_64` (`javac 21.0.12.1`, Temurin). Not on PATH — builds must set `JAVA_HOME` explicitly. |
| Node.js / Python / pandoc | NO | None on PATH. Affects the build step (see §6). |
| Existing `docs/java-fs-guide/` or `docs/harsh-guide/` | ABSENT | Workspace was empty, so **no FS-guide chapter links** (`chNN-slug`) can be emitted. The `fs_link` field stays null until a guide exists. |
| Existing `docs/inputs/interview-sources/` | ABSENT | No user-provided sources yet. Harsh can drop notes/exports there and they will be ingested as type `user-provided`. |

Assumed versions for all answers (stated in front matter too): **Java 21 (LTS)**, **Spring Boot 3.x / Spring Framework 6.x**, **Jakarta EE namespace (`jakarta.*`)**, Hibernate 6.x, JUnit 5, Kafka 3.x, PostgreSQL 15+. Anything version-sensitive outside this gets a `[VERIFY: ...]` marker.

## 2. Mode decision

Web access exists, so this runs in **EVIDENCE MODE**, not PATTERN MODE. Company tags are allowed, but only where a `sources.yml` entry exists for a page opened in-session. If Step 1 shows that search engines are unreachable and only a handful of pages can be opened directly, the project degrades to a **hybrid**: a small evidence-tagged core plus an E0 majority, and the front matter says so.

## 3. Directory layout

```
docs/qb/
  PROMPT.md            master prompt, verbatim
  00-plan.md           this file
  state.md             progress ledger: sections done, counts, open items
  review-log.md        per-section review findings
  sources.yml          source registry (S001...)
  data/
    S01.yml ... S21.yml    one record per question
  code/
    Sxx/QID/Main.java      every snippet, compiled and run
    run-all.ps1            compiles + runs everything, captures real output
  build/
    build.ps1            data -> HTML / JSON / PDF
    template.html        single-file handbook shell (inlined CSS+JS)
  dist/
    java-interview-handbook.html
    questions.json
    java-interview-handbook.pdf
```

## 4. Question record schema (`docs/qb/data/Sxx.yml`)

```yaml
- id: JH-S05-001                # JH-<section>-<3-digit number>
  section: S05
  section_title: Multithreading and concurrency
  topic: Thread pools           # fine-grained, used for filter chips
  question: >
    ...                         # in my own words, never copied
  level: L2                     # L1|L2|L3|L4
  level_basis: est.             # "source" if a source states the level, else "est."
  round: Technical              # Screening|Technical|Machine-coding|Design|Managerial|HR
  evidence: E0                  # E0|E1|E2|E3  (= count of independent sources)
  frequency: est. common        # E0: "est. very common|common|occasional" (estimate)
                                # E1+: "seen in k sources"
  companies:                    # ONLY when evidence >= E1; each needs real source ids
    - name: ""
      sources: []               # [S001, S017]
      year: null                # year of the interview if the post states it
  company_types: [Product, BFSI-Fintech]   # Service|Product|BFSI-Fintech|Startup|MNC-captive
  resume_hooks: [R:Concurrency, R:Framework]
  harsh_status: GAP             # STRONG|SHAKY|GAP|UNTESTED
  answer: >
    ...                         # spoken-style, 30-90s, mechanism first
  keywords: [..., ...]          # 4-8 must-hit terms
  followups:
    - q: ...
      a: ...                    # 2-3 of these
  trap: >
    ...                         # the plausible-sounding wrong answer
  fs_link: null                 # chNN-slug once the FS guide exists
  code:                         # optional
    path: code/S05/JH-S05-001/Main.java
    executed: true
    output: |
      ...                       # real captured stdout only
  verify: []                    # ["[VERIFY: check JEP 444 final status in JDK 21 release notes]"]
  notes: ""
```

Invariants enforced by the build check:
1. `evidence` must equal the number of distinct source ids across `companies[].sources`, except `E0` which must have `companies: []`.
2. Every source id referenced must exist in `sources.yml` → otherwise **build fails**.
3. `E0` records must carry no company name anywhere (answer text included) and render the label "unverified pattern".
4. `id` unique; question text hashed for near-duplicate detection across sections.
5. Any `code.executed: false` renders as "not executed".

## 5. Source-collection plan (Step 1)

Order of work:
1. **Connectivity probe** — try a search engine results page in the browser; if blocked, fall back to direct navigation of known public interview-experience index pages and follow in-page links only.
2. **Priority companies first** (Harsh has applied to these): NPCI, JPMorgan, Synechron, Bajaj (Finserv/Finance/Auto as reported), FIS, Nomura.
3. **BFSI and fintech** next, since that is the target market: Goldman Sachs, Morgan Stanley, Barclays, Deutsche Bank, Citi, Wells Fargo, BNY, Amex, Visa, Mastercard, Fiserv, PayPal, PhonePe, Razorpay, Paytm, CRED, Juspay.
4. **Product**: Amazon, Microsoft, Adobe, Atlassian, Walmart, Flipkart, Oracle, Salesforce, Intuit, ServiceNow, SAP.
5. **Service-based**: TCS, Infosys, Wipro, Cognizant, Capgemini, Accenture, HCLTech, LTIMindtree, EPAM, GlobalLogic.

Per-source rules applied: open the page myself; skip paywalled/login-only pages and never work around access controls; record date accessed; prefer posts from roughly the last 3 years and mark older ones `dated`; extract questions in my own words with a hard 12-consecutive-word limit; store no personal details about post authors; treat copies/reposts as one source.

Search query shapes to use: `<company> java developer interview experience <year>`, `<company> backend interview rounds spring boot`, `<company> SDE interview questions java 2 years experience`, plus company career pages describing their hiring process.

Step 1 ends with a report: sources per company, per evidence level, per year — then STOP for approval.

## 6. Build plan (and its risks)

- **JSON** (`dist/questions.json`): emitted directly from the YAML by a PowerShell script. No external runtime needed.
- **HTML**: a single self-contained file — the question data is inlined as a JSON blob, with vanilla JS for instant search, filter chips (level, company, company type, topic, round, evidence, Harsh status, resume hook), per-question "show answer" toggle, a "mark as practiced" counter in `localStorage` wrapped in try/catch, light/dark theme, responsive layout and a print stylesheet. No build toolchain required.
- **YAML parsing**: PowerShell 5.1 has no YAML parser and there is no Node/Python. Two options, tried in this order (max two attempts, per the prompt):
  1. write the data files in a strict, machine-simple YAML subset and parse it with a small purpose-built PowerShell parser;
  2. if that proves fragile, switch the data format to **JSON** (`data/Sxx.json`) — still one record per question, same schema, parsed natively by `ConvertFrom-Json`. This is the likely outcome and will be reported as a (minor) degradation.
- **PDF**: no pandoc, no LaTeX, no Node. Plan: print the generated HTML to PDF via an installed Chrome/Edge in headless mode (`--headless --print-to-pdf`), using the print stylesheet. If no Chromium binary is available, the PDF degrades to "open `dist/java-interview-handbook.html` and print to PDF", and that degradation is reported rather than silently skipped.
- **Java code**: `code/run-all.ps1` sets `JAVA_HOME` to the bundled Temurin 21 and compiles+runs every snippet, writing real stdout back into the record. Anything that fails to compile is fixed or deleted — never shipped.

## 7. Section order and counts

Writing order (per the prompt): **S05, S06, S07, S09, S01, S02, S03, S04, S10, S11, S08, S12, S14, S17, S18, S19, S13, S15, S16, S20, S21.**

| ID | Section | Target |
|---|---|---|
| S01 | Core Java language and OOP | 70 |
| S02 | Collections internals | 70 |
| S03 | Java 8–21 features and Streams | 80 |
| S04 | JVM, memory, GC, class loading | 60 |
| S05 | Multithreading and concurrency | 100 |
| S06 | Spring Core and Spring Boot | 90 |
| S07 | Spring MVC, REST, Security, Data JPA, Hibernate | 100 |
| S08 | Microservices, Spring Cloud, resilience | 70 |
| S09 | Databases (SQL, indexing, transactions, PG/MySQL/Mongo/Redis/ES) | 120 |
| S10 | Messaging: Kafka and RabbitMQ | 70 |
| S11 | Security and identity: OAuth2, OIDC, JWT, SAML, SCIM, PKI/TLS | 70 |
| S12 | Design patterns and LLD | 60 |
| S13 | System design (HLD) | 40 |
| S14 | DevOps and cloud | 100 |
| S15 | Testing | 40 |
| S16 | Integration, ETL and iPaaS | 40 |
| S17 | Production troubleshooting and scenarios | 50 |
| S18 | Java machine-coding problems (non-LeetCode) | 40 |
| S19 | Resume and project deep-dive | 60 |
| S20 | Behavioral and HR | 50 |
| S21 | Playbooks | — |
| | **Total** | **~1,300–1,500** |

Targets are ceilings-with-judgement: duplicates get merged, weak questions get dropped, no padding.

## 8. Hot List scoring (shown in the output)

```
score = 3*resume_hook_weight + 2*evidence_weight + 3*status_weight + 2*level_weight
  resume_hook_weight : 2 if a hook matches a core resume area, 1 if peripheral, 0 if none
  evidence_weight    : E3=3, E2=2, E1=1, E0: very common=1, common=0.5, occasional=0
  status_weight      : GAP=3, SHAKY=2, UNTESTED=1, STRONG=0
  level_weight       : 2 if level in {L2, L3}, 1 if L1 or L4
```
Ties break on GAP first, then higher evidence. The rule and each question's computed score are printed in the Hot List so the ranking is auditable.

## 9. Open questions for Harsh

1. Does a `docs/java-fs-guide/` exist elsewhere that should be copied in? Without it, `fs_link` stays empty everywhere.
2. Any saved interview posts/screenshots to drop in `docs/inputs/interview-sources/`? Those become real E1+ evidence and materially improve the company tags.
3. Confirm which "Bajaj" entity (Finserv / Finance / Auto / Allianz) to prioritise, since their hiring processes differ.

## 10. Status

Step 0 complete. Awaiting the go-ahead to run **Step 1 (research)**.

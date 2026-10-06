# Review log

## 2026-10-06 - Interview-Notes Merge (user-provided PDF)
Merged `Java_Backend_Interview_QnA (2).pdf` (112 items). 36 items mapped to existing questions and 76 items became 75 new records across S01-S12, S14, S17, S18 and S19. See `merge-map-2026-10-06.md`.

**Checks run:** `check-tags.ps1` PASS on 1,458 questions; six negative tests failed as they should; `build-preview.ps1` built HTML, questions.json and indexes.json; unique ids and question texts; every answer at least 65 words; Hot List sorted with valid links; all resume-trigger, gap and playbook references resolve; the 16 new Java samples compiled and ran on OpenJDK 21.0.10 and their recorded output matches a second execution; 58 of 59 recorded samples reproduce (JH-S18-019 is pre-existing and timing-dependent); the HTML loaded in a DOM engine in normal and print modes with 1,458 questions, 1,458 answers and no script errors.

**Manual review decisions:** an existing question was tagged only when its answer would cover the PDF item; broader or differently framed items became new questions with a Related list instead of being forced onto a nearby one. Corrections to the notes: the RANK versus DENSE_RANK remark in item 6.5 (annotated on JH-S09-001), the always-O(log n) claim in item 2.3, and the Java 25 reference in item 4.11 (marked VERIFY). The notes' first-person project claims in items 7.19 and 9.2 were replaced by placeholders.

**Open checks:** the five new VERIFY entries (4.11, 5.7, 5.8, 5.9, 5.14) need a documentation check. 'Yash' may mean YASH Technologies and 'Morgan Stanley (Synechron)' does not say who interviewed; both are kept as written. No interview dates exist for S007-S015. PDF not regenerated.

## 2026-10-06 - Full-Count Continuation and Publication

**Starting point actually inspected:** 1,232 canonical records, pending database ranges 028-075 and 076-120, pending coding range 004-020, and 40 existing coding source directories. Earlier narrative reports were not treated as proof of saved work.

**Completed:** merged 93 pending database records and 17 pending coding records using structured parsing, conflict detection and schema checks. Authored S08-050 through S08-070 after reviewing the existing 49 topics. Authored S18-021 through S18-040 after reading each corresponding program. Captured execution output only after compilation and successful run. Total is 1,383 across 21 sections; every numeric section target is met. E0 1,382; E1 1; E2/E3 0. No new interview-source claims or website visits.

**Code review scope:** records state the actual input contracts and limitations, including single-threaded examples, in-memory durability, fake-clock arithmetic bounds, mutable element ownership and unsupported reentrant submissions. Made the catalog sample's printed map order deterministic. All 43 programs compiled and ran and their exported output was checked against another execution. Compilation plus example assertions is not a full stress test, production certification or database integration test.

**Schema defect found and repaired:** the initial broad count gate missed two absent company_types arrays and twelve absent empty resume_hooks arrays. The missing fields caused full-browser rendering to throw and a 39-page PDF to contain only partial content. Restored the HR empty arrays and editorial Product/Service type estimates on the two affected records without changing answers. The build now rejects missing required fields and non-array collection fields. Updated matching pending data too so rerunning the merge remains conflict-safe.

**PDF defect prevention:** count of pages alone was insufficient. The exporter now dumps the print-mode DOM, strips scripts, verifies exactly one rendered question body and answer per exported record, saves a static print HTML, then prints that script-free snapshot. A first preflight counted the JavaScript template string as an extra question; moving script removal before counting corrected the check. Final preflight: 1,383 bodies and 1,383 answers. Full PDF: 1,500 page objects, valid header, 15,797,403 bytes. Page-by-page editorial pagination review was not performed. Offline host/proxy restrictions remain enabled; no external assets are required.

**Browser:** added 50-record pagination to avoid rendering the entire answer bank for every input. Search and filters operate on all records; anchors select the appropriate page; print mode includes every answer and restores the interactive page/filter state afterward. Pointer tests passed in a new visible preview after hidden-tab actionability prevented clicks in the older shared tab. Verified next page, S18's 40 records, exact-ID search, practice persistence with test changes restored, E1 count 1, empty state, distant S21 deep link, print count 1,383 and 21 chapters, restore behavior and 390px horizontal fit.

**Quality and remaining work:** no answers below the 65-word build floor; no exact duplicate answer strings. All answers have required metadata, keywords and follow-ups. Counts and structural checks do not prove semantic uniqueness or technical correctness. The expanded bank includes 309 explicit VERIFY entries (S04 6, S05 8, S06 11, S07 33, S09 87, S10 54, S11 53, S14 22, S16 12, S17 23); these remain visible, not silently cleared. Personal missing facts remain placeholders. Database query examples were not executed; exact third-party/framework behavior still needs verification for the target versions. No full market-evidence or E2/E3 claim is made.

**Reproducibility:** build/build.ps1 -RequireFullTarget now enforces the overall range and each numeric section target before publishing, then schema/evidence rules, Java execution, output matching, index references and print preflight. HTML, PDF, questions.json, indexes.json and the static print HTML are derived from canonical data. Historical compact-edition log entries below are superseded for current counts, not erased.

## 2026-10-06 - Compact First Edition Publication

**Outcome:** 172 questions across S01-S21. This is NOT the original 1,300-1,500-question depth target. The published front matter and section coverage disclose the imbalance: 75 concurrency, 28 Spring Core, and 3-6 questions in each other section. No weak generated variations were added merely to claim the target count.

**New writing:** S01 3, S02 3, S03 3, S04 3, S07 5, S08 3, S09 6, S10 3, S11 5, S12 3, S13 3, S14 5, S15 3, S16 3, S17 3, S18 3, S19 5, S20 4, S21 3. Every new record is E0 with estimated frequency/round/level, a mechanism-first answer or personal skeleton, 4-8 keywords, 2 follow-ups and a trap. No new VERIFY markers. Personal numbers and ownership are restricted to the supplied resume; missing details are placeholders.

**Technical and duplicate review:** explicit distinctions retained between row version conflicts and cross-row write skew, cache invalidation and coalesced refresh, local events and outbox delivery, proxy interception and transaction rollback policy, and task arrival versus business success. Exact duplicate IDs and question text are rejected by the build. This does not claim automated semantic equivalence detection. SQL drills explain query construction and edge cases but were not executed; no database engine or Spring test runtime was installed. Several important original subtopics remain absent or shallow, including extended SQL query sets, token-bucket code, provider SDK specifics and full HLD case studies.

**Evidence correction:** reopened only the approved GeeksforGeeks S003 page and confirmed its explicit interview date 2021-03-13 and six-year Java developer context. S05-010 remains E1. Four previously attributed S001 questions are now E0 because the original page was partially login-gated. Other historical sources remain in the registry but are ineligible for current tags. Dates previously inferred from publication or relative age were corrected to unknown. The registry is now JSON-compatible YAML 1.2 and is parsed with a structured API; no ad hoc YAML parser. Tag validation checks source existence, eligibility, approved hostname, matching company/year, mapped question and independent-source counts. Final mix: E0 171, E1 1, E2/E3 0.

**Code:** three new Java 21 programs implement a synchronized access-order LRU, a controllable-clock TTL cache and checked paid-order aggregation. Each was compiled and executed with deterministic assertions. All six programs pass; actual stdout is embedded in the data and checked against another run. Cache limitations and algorithmic complexity are explicit. No concurrency stress proof or production-readiness claim is made.

**Artifacts:** self-contained HTML, questions.json, indexes.json and PDF. Hot List uses a visible multiplicative resume/evidence/status/level score with deterministic tie-breaking. Resume Trigger Index maps nine supplied fact groups, with direct drills and broader hook-derived references. GAP/SHAKY list, section and company coverage, six thin-evidence priority summaries, three E0 company-type playbooks and source appendix are generated. Only the eligible source URL is an active external link; excluded historical URLs remain inert text.

**Browser and print:** tested 172 question nodes, 21 chapters, 100 ranked links, E1 filter returning one item, anchor expansion, print-all even from a filtered view, restoration after print and no horizontal overflow at 390px. The full PDF has 231 page objects and a valid header. It was exported using installed Chrome with no downloaded tooling. The first export logged Chrome background service attempts; exporter was hardened with blocked host resolution and an unavailable local proxy, then successfully exported again. The document itself has a restrictive CSP and no external asset/network dependencies. PDF pagination was not manually reviewed page by page.

**Remaining limits:** original depth target unmet; two historical S05 VERIFY markers; personal answer placeholders; weak company coverage and no current lateral-process guarantees; no FS-guide links because that guide does not exist. Full build command is build/build.ps1. Prior log entries below are historical and do not override these corrected counts or eligibility decisions.

## 2026-10-06 - S06 Continuation (017-028)

- Added 12 questions: application events, transaction-phase delivery, probes, SmartLifecycle, ordering, conditional features, context tests, live configuration, dependency alignment, Boot 3 migration, context hierarchy and request-scope activation.
- New evidence mix: E0 x12, E1/E2/E3 x0. S06 totals: 28 E0. Overall: 103 questions, E0 x98 and E1 x5. No website visits or company claims were added; approved-domain restriction remains in force.
- Manual duplication review separated event dispatch from transaction timing, configuration binding from refresh lifecycle, and singleton scope from context lookup and active request scope. Exact duplicate-text checks also passed.
- Passed S06 structural checks: 4-8 keywords, 2-3 follow-ups, 75-word spoken-answer floor, E0 company exclusion and exact question-text uniqueness. The word floor is a heuristic, not measured speaking time. Source-id checks passed across both sections.
- All three existing Java samples compiled and ran with unchanged output. No new snippets were included and no Spring runtime integration tests are claimed. Editor diagnostics found no S06 errors.
- HTML/JSON rebuilt. Shared browser verified 103 total entries, 28 S06 entries, search for 018 and answer disclosure. Left S06 selected.
- Zero new VERIFY items; the two historical S05 checks and older source-access/date review remain unresolved. S06 is incomplete; next ID is JH-S06-029. PDF and indexes remain pending.

## 2026-10-06 - S06 Initial Batch and Research Restriction

- User requested approved, trusted research sites only. Saved in persistent user memory and state.md: ByteByteGo, GeeksforGeeks and AmbitionBox hosts only; ask before other domains or off-list redirects. Do not bypass access controls or execute downloaded content. Reputation does not prove a page safe or its interview claims correct.
- No web source was visited for this batch. Historical off-list records were preserved, not reused for new evidence.
- Wrote data/S06.json: 16 questions, E0 x16, E1/E2/E3 x0, five GAP questions on proxies/configuration interception/circular dependencies. Zero new [VERIFY] items; two existing S05 markers remain.
- Covers injection, registration, candidate resolution, scope, lifecycle, proxy mechanisms, self-invocation repair, cycles, configuration enhancement, post-processors, auto-configuration, typed settings, effective property origins and startup diagnosis.
- Reviewed for duplicate mechanisms: proxy mechanics, proxy-type limits and repair boundaries are distinct. Singleton concurrency remains in S05; S06's scope question concerns instance resolution and ownership. No candidate implementation details or unsupported company names were added.
- Validation passed: source-reference check across 91 records; S06 keyword/follow-up counts, spoken-answer word floor, exact question uniqueness and E0 company exclusion. Editor diagnostics found no errors.
- All three existing Java examples compiled and ran. S06 includes no code snippets and makes no claim of a Spring runtime integration test.
- Rebuilt HTML and JSON, replaced the S05-only browser heading/notice, and added a section filter. Browser checks passed for 91 total entries, 16 S06 entries, five S06 GAP entries, answer disclosure and no horizontal overflow at 390px. Left the browser on S06.
- S06 is an initial batch, not the approximate 90-question section target. PDF, indexes and remaining sections are pending; older source-access/date uncertainties remain open.

## 2026-10-06 — S05 part 1 (JH-S05-001 … JH-S05-025)

**Written:** [docs/qb/data/S05.json](docs/qb/data/S05.json) — 25 of a target 100, so S05 is part 1 of about 4.

**Evidence mix (count corrected during part 2 review):** E0 ×20, E1 ×5, E2 ×0, E3 ×0.
E1 tags and the sources they resolve to:
- JH-S05-004 synchronized method vs class level — NPCI, S001, 2026
- JH-S05-009 deadlock conditions and avoidance — NPCI, S001, 2026
- JH-S05-010 avoiding deadlock without synchronized — JPMorgan Chase, S003, 2021 (dated, L3)
- JH-S05-015 ThreadPoolTaskExecutor — NPCI, S001, 2026
- JH-S05-019 CompletableFuture mechanism and methods — NPCI, S001, 2026

Every other record is E0 and carries company *type* tags only, never a company name.

**Code:** 2 samples, both compiled and run on Temurin 21.0.12.1 via `code/run-all.ps1`; the real stdout is stored in the records.
- JH-S05-017 confirms submit() swallows the exception into the Future while execute() reaches the UncaughtExceptionHandler.
- JH-S05-019 confirms thenApply/thenCompose/thenCombine results and that a failed stage skips downstream stages.

**[VERIFY] items: 2**
- JH-S05-023 — ScopedValue preview vs final status in the target JDK.
- JH-S05-024 — whether blocking inside `synchronized` still pins a virtual thread to its carrier in the target JDK.

**De-duplication:** JH-S05-009 and JH-S05-010 both concern deadlock and were kept separate deliberately — the first is the general mechanism (E1, NPCI), the second is specifically "without synchronized" (E1, JPMorgan) and answers a different framing. They cross-reference each other. Merge them if the handbook later feels repetitive.

**Deliberately deferred out of S05:**
- HashMap internals and HashMap vs ConcurrentHashMap (S001, S004) → S02, where the internals belong.
- transient vs volatile (S001) → S01.
- Producer-consumer as a coding exercise → S18; S05 keeps only the design discussion.
- Rate limiting a REST API (S003) → S08/S11.

**Not yet done / honest gaps:**
- `build/check-tags.ps1` was written but not executed in this session, so the tag integrity check is unverified by run. It must pass before any build.
- Data is JSON, not YAML, as the plan's fallback anticipated: PowerShell 5.1 has no YAML parser and there is no Node or Python available. Schema is otherwise unchanged. This is the one documented degradation so far.
- Only 6 sources exist, 4 of them usable, so the evidence base is thin and the E0 share will stay high until more research passes run. Synechron and Bajaj still have zero sources.

## 2026-10-06 - S05 Part 2 (JH-S05-026 through JH-S05-050)

**Written:** data/S05.json now contains 50 questions. The new batch is E0 x25, E1/E2/E3 x0; the section totals are E0 x45, E1 x5, E2/E3 x0. E0 frequency, level and round are explicitly estimates. No new company evidence was collected or claimed.

**Coverage:** interruption, timeout versus cancellation, safe publication, lazy initialization, multi-field invariants, coalesced token refresh, concurrent traversal, read-mostly collections, parallel-stream side effects, executor dependency starvation, scheduling, completion-order processing, fan-out failure policy, context restoration, transaction boundaries, permit ownership, race testing, measurement, diagnosis, per-key ordering, Phaser, immutable task inputs, local versus distributed synchronization, CPU cancellation and resource budgets.

**Review corrections to part 1:**
- Replaced the physical-memory explanation of volatile with its happens-before contract.
- Removed the claim that every atomic increment is a CAS loop and that LongAdder allocates one cell per thread. Removed biased-locking advice for Java 21.
- Removed invented token-cache coordination and per-connector pool isolation. Missing resume implementation details now use [[HARSH TO FILL: ...]].
- Corrected concurrency readiness overstatements in 006, 013 and 014 to SHAKY. The specifically assessed exception-handling gap remains GAP in 017.
- Marked the four L2 levels attached to the one-year NPCI report as estimates, not source-stated L2 levels.
- Clarified that a ThreadFactory uncaught-exception handler does not expose unread submit failures, and that live non-daemon workers can prevent JVM exit after shutdown.
- Reduced 019 to eight keywords and corrected the earlier evidence-count arithmetic.

**Validation actually run:**
- build/check-tags.ps1: PASS, all company source IDs resolve; 50 unique question IDs; no E0 company tags.
- PowerShell JSON parse and structural assertions: PASS for all 50 records, 4-8 keywords, 2-3 follow-ups, at least 75 words in each spoken answer, no exact duplicate question text. The word floor is a heuristic, not measured speaking time.
- Editor JSON diagnostics: no errors.
- code/run-all.ps1: both existing Java programs compiled and ran on the bundled JDK 21. Output matched their stored records. No new executable snippets were added.

**De-duplication:** manual mechanism review plus exact-text checking. Kept related prompts only where they test a distinct contract: 026 explains interruption while 049 tests cancelled-future versus running-task state; 028 covers initial publication while 047 covers post-handoff mutation; 020 defines combinators while 038 tests failure policy; 025 names synchronizers while 041 tests asynchronous permit lifetime. No automated semantic duplicate detector is claimed.

**Open checks:** zero new [VERIFY] markers; the section still has two existing markers in 023 and 024 for target-JDK-specific ScopedValue and virtual-thread pinning behavior. Existing source metadata was not independently revalidated in this writing pass. Source-ID resolution is not proof of source independence, interview year or access eligibility; the partially gated S001 and its dates need review before final publication. No HTML/PDF build or Hot List was attempted in this continuation.

## 2026-10-06 - S05 Part 3 (JH-S05-051 through JH-S05-075)

**Written:** data/S05.json now has 75 questions. New batch: E0 x25, E1/E2/E3 x0. Section totals: E0 x70, E1 x5, E2/E3 x0. All new level, round and frequency tags are estimates. No source collection or additional company attribution was performed.

**Coverage:** singleton bean state, busy waiting, unresolved futures after rejection, deadline accounting, bulk execution contracts, bounded queue trade-offs, afterExecute instrumentation, completion-observer failure, continuation executor ownership, Java 21 executor close, virtual-thread local-state costs, cancelled backlog retention, retry scheduling, pipeline drain order, linearizability, optimistic-read validation, tenant fairness, file-write coordination, resource replacement, callbacks under locks, latch outcomes, async test completion, security identity, telemetry overload and parallel reduction contracts.

**Technical review:** repaired 021's assertion that whenComplete cannot change downstream outcome. It preserves the source but can fail its dependent stage if an observer throws after source success. Also removed the claim that handler exceptions are always CompletionException wrappers or necessarily originate on another thread.

**Executable verification:** added code/S05/JH-S05-058/Main.java. It asserts that source success survives an observer failure, the returned stage fails with that observer exception, and an existing source failure takes precedence when the observer also fails. All stages are already complete before inspection; no timing sleeps or scheduling assumptions are used. code/run-all.ps1 compiled and ran all three examples successfully using the bundled JDK 21. Actual output is stored with 058; existing outputs remained unchanged.

**Data validation:** build/check-tags.ps1 passed for all 75 questions. PowerShell assertions passed for unique question text, 4-8 keywords, 2-3 follow-ups and the 75-word spoken-answer floor. The new batch contained exactly 25 E0 records with empty company tags. No measured speaking-duration claim is made.

**De-duplication review:** kept only distinct contracts within related topics: 053 tests abandoned future state rather than policy definitions; 057 implements outcome inspection beyond 017's exception-capture explanation; 058 tests observer failure precedence; 060 tests blocking at a resource-scope return boundary; 064 tests pipeline shutdown order; 066 tests validation placement; 069 tests reclamation after publication; 071 distinguishes arrival from success; 072 tests a final exactly-once assertion; 075 tests reduction algebra rather than external side effects. Exact-text checks supplement, but do not replace, this manual review.

**Open checks:** no new [VERIFY] items; two remain in 023 and 024. Source S001's public-access eligibility and interview-date metadata still need review before final publication. The source-id check verifies references, not those factual properties. No HTML/PDF, indexes or Hot List were built. Next continuation begins at JH-S05-076; weak or duplicate material should be dropped instead of forcing the approximate 100-question target.

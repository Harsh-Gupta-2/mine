<a id="ch20-toolkit"></a>
# 20 / Appendix: Interview Toolkit and Index

**Use this appendix to retrieve, compare and explain.** It was written after the technical chapters and the Part A capstone. It does not replace their mechanisms or turn local model checks into production evidence. IntegrationHub remains fictional.

**Version assumptions:** inherit each linked chapter's version and verification notes. A revision table is not a new claim of cross-version compatibility. **Execution status:** this appendix contains no application implementation; its catalog-derived index and internal links are publication-checked. Java, provider and deployment exercises retain their original NOT EXECUTED status.

## Big Picture

```mermaid
flowchart LR
  PROMPT["CLIENT: Interview question"] --> DEFINE["SERVICE: Requirement and invariant"]
  DEFINE --> CHOOSE["SERVICE: Compare viable options"]
  CHOOSE --> TRACE["SERVICE: Trace the mechanism"]
  TRACE --> BREAK["SERVICE: Failure and recovery"]
  BREAK --> PROVE["DATA: Evidence and limitations"]
  PROVE --> INDEX["DATA: Return to the owning chapter"]
  classDef client fill:#dbeafe,stroke:#1d4ed8,color:#172554
  classDef service fill:#dcfce7,stroke:#166534,color:#14532d
  classDef data fill:#fef3c7,stroke:#854d0e,color:#713f12
  class PROMPT client
  class DEFINE,CHOOSE,TRACE,BREAK service
  class PROVE,INDEX data
```

## What You Will Be Able to Explain

- Compare technologies from constraints rather than preference.
- Build a 30-day revision loop covering every chapter, including payments and the capstone.
- Retrieve precise definitions and distinguish commonly confused terms.
- Find every registered concept at its owning explanation.
- Describe what was executed, what was only reviewed and what remains uncertain.

> [!PRODUCTION]
> **Where this lives in IntegrationHub.** Every revision exercise returns to one fictional job, one tenant boundary or one failure. Use [24](24-capstone.md#ch24-capstone) to join the pieces, and [00](00-master-map.md#ch00-master-map) to recover the recommended reading order. Do not present these examples as personal employment experience or a real company's design.

<a id="ch20-decisions"></a>
## 1. X versus Y Decision Tables

The choices below are starting rules, not universal winners. “Avoid” means the stated requirement conflicts with that option, not that the tool is inherently bad. Follow the source link for the mechanism, failure modes and version qualifications.

### Runtime, Application and Data

| Choice | Use when | Avoid when | Explain through |
|---|---|---|---|
| Value object versus entity | Value object for immutable value semantics; entity for identity across change | Mutable equality keys or identity inferred from all current fields | [01](01-java-jvm.md#ch01-java-jvm), [25](25-architecture.md#ch25-architecture) |
| synchronized versus atomics | Monitor for a compound critical section; atomics for suitable single-state transitions | A multi-field invariant is split across unrelated atomics | [02](02-concurrency.md#ch02-concurrency) |
| Platform versus virtual threads | Platform workers for bounded CPU work; virtual threads for supported blocking workloads | Cheap waiting is mistaken for more database or provider capacity | [02](02-concurrency.md#ch02-concurrency), [23](23-jvm-performance.md#ch23-jvm-performance) |
| Explicit Spring configuration versus auto-configuration | Explicit beans for owned policy; defaults for verified conditional wiring | Hidden conditions or overrides make the actual runtime graph unclear | [03](03-spring.md#ch03-spring) |
| Optimistic versus pessimistic locking | Version checks for tolerable conflict; locks for required serialization under a bounded transaction | Retries hide external effects or long locks hold scarce resources | [04](04-jpa.md#ch04-jpa), [06](06-databases.md#ch06-databases) |
| Offset versus keyset pagination | Offset for shallow arbitrary page access; keyset for stable indexed traversal | Concurrent shifts or deep skipping violate the API's promised behavior | [05](05-apis-realtime.md#ch05-apis-realtime) |
| Polling versus SSE versus WebSocket | Polling for simple bounded freshness; SSE for server updates; WebSocket for justified bidirectional messaging | Persistent connections have no replay, authorization or backpressure contract | [05](05-apis-realtime.md#ch05-apis-realtime) |
| Blocking MVC versus reactive processing | Blocking style for understandable bounded request work; reactive for demand-aware pipelines and supported nonblocking dependencies | Blocking calls occupy event loops or virtual threads are claimed to provide backpressure | [05](05-apis-realtime.md#ch05-apis-realtime) |
| PostgreSQL versus document storage | Relational invariants and joins versus an appropriately bounded document model | Product labels substitute for access-pattern and consistency analysis | [06](06-databases.md#ch06-databases) |
| B-tree versus LSM design | Evaluate point/range access, write patterns and maintenance costs under the selected engine | A generic write/read slogan replaces actual compaction or index evidence | [06](06-databases.md#ch06-databases) |
| Cache versus authoritative store | Cache for reconstructible acceleration with a staleness policy; authority for accepted facts | A stale cache authorizes spending or proves durable completion | [06](06-databases.md#ch06-databases), [27](27-payments.md#ch27-payments) |
| Kafka versus RabbitMQ | Retained partitioned log versus routed broker work under the chosen product semantics | Ordering, retention, acknowledgements and replay remain unspecified | [07](07-messaging.md#ch07-messaging) |
| Outbox versus direct dual write | Outbox for local state plus publication intent; direct calls only with explicit gap recovery | Two independent writes are described as atomic | [07](07-messaging.md#ch07-messaging) |
| Event sourcing versus state storage | Authoritative event history when reconstruction and domain history justify it; current state otherwise | Replay, event evolution and operational costs are not owned | [07](07-messaging.md#ch07-messaging) |
| Batch versus streaming | Batch for bounded inputs and relaxed freshness; streaming for justified continuous decisions | Low latency is pursued without late-data or replay policy | [07](07-messaging.md#ch07-messaging), [26](26-data-platform.md#ch26-data-platform) |
| ETL versus ELT | Transform before load for minimization; transform after controlled landing for governed reprocessing | Raw sensitive data lands without permission or source replay is assumed | [13](13-integration.md#ch13-integration), [26](26-data-platform.md#ch26-data-platform) |
| Warehouse versus lake versus lakehouse | Governed analytical tables, retained diverse objects, or table-managed object data | Terminology is treated as a quality or transaction guarantee | [26](26-data-platform.md#ch26-data-platform) |

### Security, Architecture and Delivery

| Choice | Use when | Avoid when | Explain through |
|---|---|---|---|
| OAuth versus OIDC versus SCIM | Delegated API authority, login identity and provisioning respectively | One protocol is assumed to perform all three roles | [08](08-security.md#ch08-security) |
| JWT local validation versus introspection | Local validation for bounded independent checks; introspection for an online authority contract | Immediate revocation is promised without a mechanism or online dependency budget | [08](08-security.md#ch08-security) |
| React local versus shared state | Local state for component-owned interaction; shared state for real cross-view ownership | A global store hides request identity and tenant-generation races | [09](09-frontend.md#ch09-frontend) |
| Retry versus circuit breaker versus bulkhead | Retry recoverable attempts, stop unhealthy submissions, isolate resource budgets | Any is described as undoing an accepted remote effect | [10](10-system-design.md#ch10-system-design) |
| Strategy versus Adapter | Strategy for interchangeable behavior; Adapter for translating an external interface | Patterns are added without a changing behavior or incompatible boundary | [11](11-lld.md#ch11-lld) |
| Modular monolith versus microservices | One deployable with enforced modules versus justified independent operations | Distribution is added before ownership and data contracts exist | [25](25-architecture.md#ch25-architecture) |
| Leader lease versus fencing | Lease for leadership policy; effect-side fence to reject obsolete epochs | Permission expiry is assumed to stop already-running code | [22](22-distributed.md#ch22-distributed) |
| 2PC versus saga | Coordinated atomic commit in a supported scope versus explicit local commits and business recovery | Compensation is described as erasing history or 2PC as removing failure blocking | [22](22-distributed.md#ch22-distributed), [27](27-payments.md#ch27-payments) |
| L4 versus L7 balancing | Transport routing versus application-aware policy | TLS termination and protocol ownership are unspecified | [21](21-network-os.md#ch21-network-os) |
| Image versus running container | Immutable packaged filesystem versus a process with runtime limits | Building an image is treated as a deployment or security proof | [14](14-docker.md#ch14-docker) |
| Readiness versus liveness | Routing eligibility versus restart policy | A temporary downstream outage restarts the entire fleet | [15](15-kubernetes.md#ch15-kubernetes) |
| Rolling versus blue-green versus canary | Gradual replacement, environment switch or measured exposure | Old and new schemas are incompatible or rollback evidence is absent | [16](16-delivery.md#ch16-delivery) |
| VM/container versus Lambda | Managed long-lived workload versus suitable bounded invocation | Runtime, state, networking, cost and concurrency constraints are ignored | [17](17-cloud.md#ch17-cloud) |
| Replication versus backup | Availability copy versus retained recovery history | Replicated corruption is mistaken for recoverability | [17](17-cloud.md#ch17-cloud), [18](18-operations.md#ch18-operations) |
| Metrics versus logs versus traces | Aggregate behavior, contextual events and request-path evidence | High-cardinality labels or missing sampling context obscure the question | [18](18-operations.md#ch18-operations) |
| Unit versus integration versus end-to-end tests | Narrow invariants, real boundaries and selected user journeys | Mock success is reported as database or browser correctness | [19](19-testing.md#ch19-testing) |
| JFR versus microbenchmark | Diagnose a representative running workload versus isolate a specific cost | Synthetic rankings are generalized to whole-system latency | [23](23-jvm-performance.md#ch23-jvm-performance) |
| Payment retry versus reconciliation | Repeat the same supported operation identity versus resolve independent evidence | A new payment is created to hide an unknown previous outcome | [27](27-payments.md#ch27-payments) |

> [!DECISION]
> **A defensible answer has a reversal condition.** State the chosen option, the requirement it satisfies, the cost accepted and the evidence that would make you change it. For HLD, use the full case-study method in [12](12-hld.md#ch12-hld), not just this table.

<a id="ch20-plan"></a>
## 2. A 30-Day Revision Plan

Treat each day as a bounded study session, not a claim about how quickly any person will master the guide. A workable allocation is reading, closed-book explanation and one evidence-based exercise; adjust duration to availability. Repeat weak topics instead of adding more sources. Where tools are unavailable, perform a trace and state NOT EXECUTED rather than invent results.

| Day | Focus | Deliverable and self-check |
|---|---|---|
| 1 | [00 master map](00-master-map.md#ch00-master-map) | Draw the request journey and identify authority at each boundary |
| 2 | [01 JVM and values](01-java-jvm.md#ch01-java-jvm) | Explain class identity, reachability and one collection mutation trap |
| 3 | [02 memory model](02-concurrency.md#ch02-concurrency) | Trace a happens-before edge and a compound operation that remains unsafe |
| 4 | [02 executors](02-concurrency.md#ch02-concurrency), [21 I/O](21-network-os.md#ch21-network-os) | Explain queue growth, blocking and event-loop ownership without equating threads with capacity |
| 5 | [21 networking](21-network-os.md#ch21-network-os) | Follow DNS through TLS and distinguish connect, read and overall deadlines |
| 6 | [03 Spring](03-spring.md#ch03-spring) | Trace request security, proxy interception and a self-invocation transaction trap |
| 7 | [04 JPA](04-jpa.md#ch04-jpa) | Explain flush versus commit and diagnose an N+1 fetch plan |
| 8 | [06 transactions](06-databases.md#ch06-databases) | Trace two concurrent writers and name the invariant their isolation must protect |
| 9 | [06 storage and recovery](06-databases.md#ch06-databases) | Explain index, WAL, vacuum, pool wait and restore boundaries; note the open SQL workbook gap |
| 10 | [05 APIs](05-apis-realtime.md#ch05-apis-realtime) | Design a versioned resource, bound idempotency result and pagination contract |
| 11 | [05 live channels](05-apis-realtime.md#ch05-apis-realtime), [09 UI](09-frontend.md#ch09-frontend) | Trace cursor expiry, snapshot recovery and tenant-switch races |
| 12 | [07 messaging](07-messaging.md#ch07-messaging) | Crash an imagined outbox/consumer flow at each commit gap and predict replay |
| 13 | [07 batch and streams](07-messaging.md#ch07-messaging) | Distinguish event, replacement state, window and restart identity |
| 14 | [13 integration](13-integration.md#ch13-integration), [26 analytics](26-data-platform.md#ch26-data-platform) | Declare mapping version, quarantine policy, fact grain and freshness cutoff |
| 15 | [08 identity](08-security.md#ch08-security) | Separate authentication, authorization, provisioning and token purposes |
| 16 | [08 protection](08-security.md#ch08-security), [09 frontend](09-frontend.md#ch09-frontend) | Review tenant enforcement, token handling, data minimization and effect cleanup |
| 17 | [10 foundations](10-system-design.md#ch10-system-design) | Estimate demand under named assumptions and identify the first capacity budget |
| 18 | [25 architecture](25-architecture.md#ch25-architecture) | Draw contexts and a Spring dependency rule; write one ADR with consequences |
| 19 | [22 distributed systems](22-distributed.md#ch22-distributed) | Explain safety, quorum assumptions, lease versus fence and unknown outcomes |
| 20 | [11 LLD](11-lld.md#ch11-lld) | Choose two cases; explain APIs, invariant, concurrency and tests before patterns |
| 21 | [12 HLD](12-hld.md#ch12-hld) | Design bulk sync and live status, including estimates, data model and failure recovery |
| 22 | [27 payments](27-payments.md#ch27-payments) | Explain wallet overspend, gateway ambiguity and ledger reconciliation as separate problems |
| 23 | [14 Docker](14-docker.md#ch14-docker), [15 Kubernetes](15-kubernetes.md#ch15-kubernetes) | Trace image to process, resource limit, readiness and graceful shutdown |
| 24 | [16 delivery](16-delivery.md#ch16-delivery) | Bind test evidence to an image and explain schema-compatible GitOps rollback |
| 25 | [17 infrastructure](17-cloud.md#ch17-cloud) | Separate routing, IAM and managed-service responsibility; outline a tested restore |
| 26 | [18 operations](18-operations.md#ch18-operations) | Define an outcome SLI, burn alert, incident timeline and recovery evidence |
| 27 | [23 performance](23-jvm-performance.md#ch23-jvm-performance) | Choose CPU, allocation, heap or thread evidence from one symptom; avoid invented measurements |
| 28 | [19 quality](19-testing.md#ch19-testing) | Map each claimed guarantee to its narrowest useful test and list untested boundaries |
| 29 | [24 capstone](24-capstone.md#ch24-capstone) | Rehearse the five-minute system script and the five failure stories closed-book |
| 30 | [20 toolkit](20-toolkit.md#ch20-toolkit) and weakest chapters | Run a mock interview, record missing mechanisms and recheck their source explanations |

> [!MECHANISM]
> **Use retrieval, then correction.** Answer without the guide, compare against the owning chapter, identify the exact missing step, and repeat the explanation with that step included. A correct keyword list is not the same as tracing a commit or a race.

<a id="ch20-glossary"></a>
## 3. Glossary of High-Risk Distinctions

The global index below covers the full registered vocabulary. This shorter glossary prioritizes distinctions that commonly change the correctness of an answer; it links out instead of repeating entire chapters.

| Term or pair | Precise working meaning | Owner |
|---|---|---|
| Authority versus projection | Accepted facts versus a derived representation with a declared lag or recovery rule | [00](00-master-map.md#ch00-master-map) |
| Reachable versus useful | GC reachability can retain objects the business no longer needs | [01](01-java-jvm.md#ch01-java-jvm) |
| Equality versus identity | Same value under an equality contract versus the same domain/runtime entity | [01](01-java-jvm.md#ch01-java-jvm), [25](25-architecture.md#ch25-architecture) |
| Happens-before | A synchronization-derived visibility/order relation, not timestamp order | [02](02-concurrency.md#ch02-concurrency) |
| Atomicity versus visibility | Indivisible relevant change versus guaranteed observation of a write | [02](02-concurrency.md#ch02-concurrency) |
| Admission versus concurrency | Bound accepted/waiting work versus active use of a resource | [02](02-concurrency.md#ch02-concurrency) |
| Proxy boundary | Calls through the eligible proxy are intercepted; ordinary self-calls may not be | [03](03-spring.md#ch03-spring) |
| Flush versus commit | Synchronize pending SQL versus accept the transaction under its durability contract | [04](04-jpa.md#ch04-jpa) |
| Idempotency | Repeating one identified logical operation does not create another accepted effect | [05](05-apis-realtime.md#ch05-apis-realtime) |
| Replay cursor | Position for requesting retained history, not a guarantee the history still exists | [05](05-apis-realtime.md#ch05-apis-realtime) |
| Backpressure | Explicit demand or bounded overload handling, not merely faster asynchronous calls | [05](05-apis-realtime.md#ch05-apis-realtime) |
| MVCC | Visibility of row versions under transaction rules; not freedom from all locks or conflicts | [06](06-databases.md#ch06-databases) |
| Replica versus backup | A live copy versus retained recovery history and a restore procedure | [06](06-databases.md#ch06-databases) |
| Outbox versus inbox receipt | Durable publication intent versus duplicate input/effect tracking | [07](07-messaging.md#ch07-messaging) |
| Event sourcing versus CQRS | Authoritative event history versus separate command and query models | [07](07-messaging.md#ch07-messaging) |
| Compensation versus rollback | A new business action after commit versus rejection of an uncommitted local transaction | [07](07-messaging.md#ch07-messaging) |
| Authentication versus authorization | Establish identity versus permit a specific resource operation | [08](08-security.md#ch08-security) |
| Provisioning | Manage account and membership lifecycle; not login | [08](08-security.md#ch08-security) |
| Access, ID and refresh tokens | API authority, client login assertion and renewal credential respectively | [08](08-security.md#ch08-security) |
| CORS versus CSRF | Browser cross-origin response policy versus unwanted credential-bearing actions | [08](08-security.md#ch08-security) |
| UI generation | Identity-lifetime marker that invalidates obsolete requests and subscriptions | [09](09-frontend.md#ch09-frontend) |
| Bulkhead | An isolated resource budget that limits failure propagation | [10](10-system-design.md#ch10-system-design) |
| Estimate versus measurement | Assumption-driven arithmetic versus observed behavior in a recorded environment | [10](10-system-design.md#ch10-system-design) |
| Invariant | A property that must remain true under the stated concurrency and failure model | [11](11-lld.md#ch11-lld) |
| Quarantine | Durable classified exception requiring a declared repair or exclusion policy | [13](13-integration.md#ch13-integration) |
| Namespace versus cgroup | Resource visibility boundary versus accounting and control | [14](14-docker.md#ch14-docker) |
| Startup, readiness and liveness | Initial allowance, routing eligibility and restart policy | [15](15-kubernetes.md#ch15-kubernetes) |
| Desired versus observed state | Declarative target versus current controller/runtime result | [15](15-kubernetes.md#ch15-kubernetes) |
| Artifact identity versus trust | Exact bytes versus provenance, approval and security evidence | [16](16-delivery.md#ch16-delivery) |
| Terraform configuration versus state | Desired declarations versus tracked remote identities and attributes | [17](17-cloud.md#ch17-cloud) |
| SLI, SLO and SLA | Outcome measure, target and external agreement | [18](18-operations.md#ch18-operations) |
| RPO versus RTO | Recovery data-loss objective versus recovery-time objective | [18](18-operations.md#ch18-operations) |
| Test double versus integration evidence | Simulated collaborator behavior versus an exercised real boundary | [19](19-testing.md#ch19-testing) |
| Readiness versus message completion | Bytes may be available without a complete application message | [21](21-network-os.md#ch21-network-os) |
| Safety versus liveness | Invalid outcomes do not occur versus progress eventually occurs under assumptions | [22](22-distributed.md#ch22-distributed) |
| Lease versus fence | Permission lifetime versus effect-side rejection of obsolete ownership epochs | [22](22-distributed.md#ch22-distributed) |
| Linearizability versus eventual consistency | Operations fit a real-time-respecting order versus replicas converge under stated conditions | [22](22-distributed.md#ch22-distributed) |
| Allocation versus retained memory | Object creation over time versus reachable objects kept alive | [23](23-jvm-performance.md#ch23-jvm-performance) |
| Heap versus process memory | Managed object region versus total runtime resource usage | [23](23-jvm-performance.md#ch23-jvm-performance) |
| Bounded context versus aggregate | Scope of a consistent domain model versus a boundary enforcing particular invariants | [25](25-architecture.md#ch25-architecture) |
| Port versus adapter | Application-facing contract versus an implementation connecting a technology | [25](25-architecture.md#ch25-architecture) |
| Grain versus dimension | Meaning of one fact row versus descriptive attributes for interpreting facts | [26](26-data-platform.md#ch26-data-platform) |
| Event time versus processing time | When the source event occurred versus when a system handled it | [26](26-data-platform.md#ch26-data-platform) |
| Lineage | Evidence connecting derived data to sources, transformations and versions | [26](26-data-platform.md#ch26-data-platform) |
| Unknown payment outcome | The caller lacks authoritative effect evidence; it is not a decline | [27](27-payments.md#ch27-payments) |
| Balanced journal versus available funds | Conservation of posted accounting effects versus permission to spend | [27](27-payments.md#ch27-payments) |
| Reconciliation | Match independent evidence at a cutoff and resolve owned exceptions | [27](27-payments.md#ch27-payments) |

> [!TRAP]
> **Do not promote a definition into a guarantee.** Naming an outbox, an aggregate, a lease or a journal does not show that the correct data shares a commit, concurrency boundary or recovery policy. Ask what enforces it.

<a id="ch20-evidence"></a>
## 4. Evidence and Answer Discipline

A concise answer follows this order: requirement, invariant, mechanism, failure, recovery, trade-off and evidence. For debugging, start from an observed symptom and choose the next discriminating check; do not list every tool you know. For design, ask which dimensions can vary: tenant, scale, latency, cost, failure isolation and regulatory scope.

When discussing the guide's labs, say which runtime and boundary were actually exercised. Local JavaScript checks do not establish Java compilation, database isolation, payment-provider behavior or deployment safety. A parsed POM is not a passing JUnit suite. A rendered Mermaid diagram is not a deployed architecture. These are useful but different evidence.

The guide has all chapter sources after this batch, but earlier editorial work remains: Chapter 06 still needs its SQL query-writing workbook and expanded Redis patterns, and the final Chapter 05/06 accuracy/coverage pass remains open. Syntax highlighting is unavailable in the current offline edition. Those limitations are not resolved by the presence of an index.

**[VERIFY: recheck version-sensitive APIs, provider/network contracts, regulatory claims and every remaining chapter verification item against current primary documentation and an approved execution environment. This appendix does not close the guide's outstanding verification or editorial gaps.]**

> [!INTERVIEW]
> **A useful uncertainty statement is specific.** “I would verify the provider's idempotency retention and status-lookup contract before retrying this unknown operation” is stronger than claiming certainty about every gateway. Pair uncertainty with the exact evidence needed.

<a id="ch20-global-index"></a>
## 5. Global Concept Index

This alphabetical index is generated from `catalog.json` during the build and links every registered term to its owning explanation. A term can have more than one owner; virtual threads deliberately links both core Java and concurrency. All 28 chapters, including this appendix and the capstone, are covered. The catalog records coverage vocabulary, not proof that every editorial or runtime requirement is complete.

{{GLOBAL_CONCEPT_INDEX}}

<a id="ch20-concept-index"></a>
## Concepts Explained Here

Use [decision tables](#ch20-decisions), the [30-day revision plan](#ch20-plan), the [glossary](#ch20-glossary) and the [global concept index](#ch20-global-index). Read [evidence discipline](#ch20-evidence) before describing lab results as production guarantees.

## Related Chapters

The index supplies direct explanations and chapter destinations for every chapter. Return to [00 orientation](00-master-map.md#ch00-master-map) or [24 the connected system](24-capstone.md#ch24-capstone) for the narrative route. Generated links are reciprocal.

<a id="ch20-cheat-sheet"></a>
## One-Page Cheat Sheet

**Answer:** requirement, invariant, mechanism, failure, recovery, trade-off, evidence. A product name is not an explanation.

**Compare:** use when, avoid when, accepted cost and reversal condition. Change the recommendation when constraints change.

**Revise:** retrieve closed-book, compare with the owning chapter, fix the missing step and repeat. Cover all 28 chapters rather than only familiar Java topics.

**Trace:** identity, authorization, commit, transport, effect, recovery and observability. Keep accepted, completed and observed separate.

**Be precise:** authority versus projection; atomicity versus visibility; retry versus reconciliation; lease versus fence; journal balance versus spendable funds.

**Report honestly:** local test, static review and unavailable execution are different. Use the complete index to find an explanation, not to infer that open verification items were closed.

<a id="ch20-interview"></a>
## Interview Corner

### Basic: How Do You Choose between Two Technologies?

State the required behavior, eliminate options that violate it, compare remaining operational costs and identify the evidence that could reverse the choice.

### Internals: What Makes an Answer More than Vocabulary?

Trace actual state changes and the enforcing boundary: lock, transaction, proxy, protocol message or durable receipt. Explain what happens when that step fails.

### Trace/Debug: You Do Not Know the Root Cause Yet.

Name one plausible hypothesis and the cheapest observation that would disprove it. Gather that evidence before changing unrelated settings.

### Scenario: A Design Works Except When a Response Is Lost.

Separate caller knowledge from remote effect. Identify the operation, stored result and recovery evidence. Add idempotency or reconciliation at the actual effect boundary.

### Basic: What Does Completing the Guide Mean?

All planned chapter sources and index destinations exist. It does not mean every external claim was verified, every lab ran or earlier editorial gaps disappeared.

### Scenario: Explain IntegrationHub without Claiming Experience You Do Not Have.

Call it a fictional reference design. Explain its requirements, mechanisms and trade-offs, then distinguish the executed local checks from proposed integration and production validation.
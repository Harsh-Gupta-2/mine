# Chapter 02 Concurrency Labs

**NOT EXECUTED:** Java 21 source is supplied, but no installed compiler is currently available through PATH or JAVA_HOME. No downloads are authorized. Compilation and runtime assertions remain unverified; strings printed by main methods are not observed output.

SnapshotLab uses an immutable tenant-limit snapshot published through a volatile reference. The reader takes one reference snapshot per iteration. Its bounded spin is a diagnostic probe, not a production waiting policy and not proof of all Java Memory Model outcomes. Final fields and the volatile handoff provide overlapping guarantees; deleting volatile is not expected to produce a deterministic test failure.

ConcurrencyLab contains arranged-contract probes:

- A barrier makes two volatile reads precede either write, deliberately losing a read-modify-write update. This is a split increment, not a probabilistic benchmark of the ++ operator.
- Two tasks exercise AtomicInteger increments and a monitor-protected one-permit invariant.
- Latches hold workers to demonstrate queue-before-noncore-growth and rejection with core=1, max=2 and queue capacity=1. Deadlines fail the test on severe scheduling delays; they are safety limits, not performance assertions.
- Explicit-executor CompletableFuture composition and per-key ConcurrentHashMap.merge.
- An interruptible semaphore wait on a virtual thread with no permit leak. The interrupt may arrive just before or during acquire; both paths must terminate.

There are no sleeps, intentionally permanent deadlocks, external services, Maven dependencies, or preview APIs. This is not IntegrationHub-lite or a production quota implementation. Passing these probes would not prove fairness, all schedules, stress safety, memory-model correctness without a reasoning argument, distributed idempotency, or pinning/performance.

Windows PowerShell from the workspace root; no administrator rights:

```powershell
& './docs/java-fs-guide/code/02-concurrency/run.ps1'
```

The wrapper records the actual outcome in execution.json. With `-JdkHome` pointing to an already installed JDK 21+, it compiles both files using --release 21, then runs them in fresh processes. It never downloads or installs tools. The compiler-present path is not verified in this environment.
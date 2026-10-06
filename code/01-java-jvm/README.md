# Chapter 01 Java Labs

**Status: NOT EXECUTED.** No JDK compiler was found locally; downloads were not authorized. These files were reviewed as Java 21 source, but compilation and runtime behavior have not been verified. Do not treat the success strings in source as observed output.

- CoreJavaLab.java: record validation and defensive copying; tenant-scoped keys; hash collisions versus equality; pass-by-value; sealed types and record patterns; generic variance; lazy, single-use Streams; duplicate-key collector failure; suppressed resource exceptions; virtual-thread identity.
- LoadingLab.java: loading without initialization, single initialization, first initialization failure and subsequent erroneous-class failure. Run in a fresh JVM so its static-state checks start clean.

The deliberately side-effecting Stream `map` is a sequential diagnostic probe, not the recommended transformation style. The constant hash in CollisionKey deliberately creates collisions and is not a production hashing strategy. These labs have no HTTP, database, or production-system dependencies and are not a runnable IntegrationHub service.

Windows PowerShell, from the workspace root, no administrator rights or downloads:

```powershell
& './docs/java-fs-guide/code/01-java-jvm/run.ps1'
```

The wrapper was executed and reported NOT EXECUTED because a JDK was unavailable. It accepts `-JdkHome` pointing to an already installed JDK 21+; it never installs one. With a compiler, it uses `--release 21`, compiles both files into out/, runs each in a fresh process, and records actual output in execution.json. The recorded status distinguishes PASS, FAIL and NOT EXECUTED.

Not tested: compilation, lab assertions, Java 21/25 cross-runtime behavior, GC behavior, JIT behavior, virtual-thread pinning/performance, or concurrency stress. GC and timing behavior are intentionally not inferred from small identity/contract tests.
# Chapter 05 API Labs

**Status: NOT EXECUTED.** No JDK compiler was found locally and downloads were not authorized. These files were reviewed as Java 21 source. Compilation, assertion results and the printed completion strings are unverified; do not read them as observed output.

- IdempotencyLab.java: tenant-scoped idempotency keys. Identical retries replay one stored result, a reused key with a different body is a conflict, two tenants may use the same client string, retention expiry ends the guarantee, and eight concurrent duplicates collapse to one acceptance through an atomic reservation.
- ReplayCursorLab.java: `Last-Event-ID` resume semantics. A cursor inside retention replays only missed events, an up-to-date cursor replays nothing, a cursor older than bounded retention falls back to a durable snapshot, a first connection is not a replay, and an impossible cursor is rejected rather than treated as caught up.

Both labs are in-process models of the contracts described in the chapter. They contain no HTTP server, no SSE or WebSocket transport, no database and no Kafka. A passing check would demonstrate the reasoning of the contract, never the behavior of Spring MVC, a proxy, a browser `EventSource` or a real broker.

Windows PowerShell, from the workspace root, no administrator rights or downloads:

```powershell
& './docs/java-fs-guide/code/05-apis-realtime/run.ps1'
```

The wrapper was executed and reported NOT EXECUTED because a JDK was unavailable. It accepts `-JdkHome` pointing to an already installed JDK 21 or later; it never installs one. With a compiler it uses `--release 21`, compiles both files into out/, runs each in a fresh process and records actual output in execution.json. The recorded status distinguishes PASS, FAIL and NOT EXECUTED.

The concurrency check uses a start gate and bounded `Future` deadlines. Those deadlines are test guards, not latency measurements, and the executor is shut down in a `finally` block. Nothing in these labs measures throughput, connection capacity or proxy behavior.

Not tested: compilation, assertion outcomes, HTTP semantics, SSE reconnection in any browser, WebSocket upgrade handling, proxy buffering, broker fan-out, or the durability properties that the chapter attributes to PostgreSQL and Kafka.

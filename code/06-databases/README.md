# Chapter 06 Database Labs

**Status: NOT EXECUTED.** No JDK compiler was found locally and downloads were not authorized. These files were reviewed as Java 21 source. Compilation, assertion results and the printed completion strings are unverified; do not read them as observed output.

- MvccLab.java: a tiny multi-version store with explicit commit-time checking. A snapshot read stays stable while another transaction commits, an unprotected read-modify-write loses an update, first-committer-wins aborts the stale writer instead, write skew commits under snapshot isolation because the two transactions write different keys, and adding a read-set check turns that write skew into a serialization failure the application must retry.
- ConnectionPoolLab.java: a bounded pool with a separate admission limit. Pool size caps concurrent database work, acquisition fails on a deadline rather than blocking forever, a leaked lease is permanent capacity loss, releasing in a `finally` block preserves capacity when a statement fails, and callers past the admission limit are rejected instead of queueing without bound.

Both labs are in-process models of the mechanisms described in the chapter. There is no database, no JDBC driver, no SQL parser and no storage engine. A passing check would demonstrate the reasoning behind an isolation level or a pool limit, never the behavior of PostgreSQL, MySQL, HikariCP or any real driver. In particular, `MvccLab` approximates snapshot isolation and serializable snapshot isolation for teaching; it is not a reimplementation of any engine's visibility rules, predicate locking or conflict detection.

Windows PowerShell, from the workspace root, no administrator rights or downloads:

```powershell
& './docs/java-fs-guide/code/06-databases/run.ps1'
```

The wrapper was executed and reported NOT EXECUTED because a JDK was unavailable. It accepts `-JdkHome` pointing to an already installed JDK 21 or later; it never installs one. With a compiler it uses `--release 21`, compiles both files into out/, runs each in a fresh process and records actual output in execution.json. The recorded status distinguishes PASS, FAIL and NOT EXECUTED.

The concurrency checks use start gates, bounded `Future` deadlines and `finally` cleanup. Acquisition timeouts are test guards, not latency measurements, and no check in either lab measures throughput, query cost or connection capacity on real hardware.

Not tested: compilation, assertion outcomes, any SQL statement, index or plan behavior, real isolation levels, locking, replication, vacuum, backup or restore. Every such claim in the chapter is reasoning to verify against the engine's documentation and your own measurements.

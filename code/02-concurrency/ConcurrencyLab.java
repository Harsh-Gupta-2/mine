import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ConcurrencyLab {
    private volatile int splitCounter;

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void stop(ExecutorService executor) throws InterruptedException {
        executor.shutdownNow();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            throw new AssertionError("Executor failed to terminate");
        }
    }

    void lostUpdateAndAtomics() throws Exception {
        var pool = Executors.newFixedThreadPool(2);
        try {
            var bothRead = new CyclicBarrier(2);
            Callable<Void> splitIncrement = () -> {
                int observed = splitCounter;
                bothRead.await(5, TimeUnit.SECONDS);
                splitCounter = observed + 1;
                return null;
            };
            var first = pool.submit(splitIncrement);
            var second = pool.submit(splitIncrement);
            first.get(10, TimeUnit.SECONDS);
            second.get(10, TimeUnit.SECONDS);
            check(splitCounter == 1, "Both volatile reads preceded either write");

            var atomic = new AtomicInteger();
            var tasks = List.<Callable<Void>>of(() -> {
                for (int count = 0; count < 1000; count++) atomic.incrementAndGet();
                return null;
            }, () -> {
                for (int count = 0; count < 1000; count++) atomic.incrementAndGet();
                return null;
            });
            var completed = pool.invokeAll(tasks, 5, TimeUnit.SECONDS);
            for (var future : completed) future.get();
            check(atomic.get() == 2000, "Atomic increments retain every update");
        } finally {
            stop(pool);
        }
    }

    static final class Quota {
        private int remaining = 1;

        synchronized boolean reserve() {
            if (remaining == 0) return false;
            remaining--;
            return true;
        }
    }

    static void monitorInvariant() throws Exception {
        var pool = Executors.newFixedThreadPool(2);
        try {
            var quota = new Quota();
            var first = pool.submit(quota::reserve);
            var second = pool.submit(quota::reserve);
            int accepted = (first.get(5, TimeUnit.SECONDS) ? 1 : 0)
                    + (second.get(5, TimeUnit.SECONDS) ? 1 : 0);
            check(accepted == 1, "One invariant protected by one monitor");
        } finally {
            stop(pool);
        }
    }

    static void queueThenGrowThenReject() throws Exception {
        var release = new CountDownLatch(1);
        var coreStarted = new CountDownLatch(1);
        var extraStarted = new CountDownLatch(1);
        var pool = new ThreadPoolExecutor(1, 2, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.AbortPolicy());
        Callable<Void> coreTask = () -> {
            coreStarted.countDown();
            if (!release.await(10, TimeUnit.SECONDS)) throw new AssertionError("Core wait expired");
            return null;
        };
        try {
            var core = pool.submit(coreTask);
            check(coreStarted.await(5, TimeUnit.SECONDS), "Core worker started");
            var queued = pool.submit(() -> "queued result");
            check(pool.getQueue().size() == 1, "Second task queued before noncore growth");
            var extra = pool.submit(() -> {
                extraStarted.countDown();
                if (!release.await(10, TimeUnit.SECONDS)) throw new AssertionError("Extra wait expired");
                return "extra result";
            });
            check(extraStarted.await(5, TimeUnit.SECONDS), "Maximum worker started");
            boolean rejected = false;
            try {
                pool.submit(() -> "must reject");
            } catch (RejectedExecutionException expected) {
                rejected = true;
            }
            check(rejected, "Full queue plus maximum workers rejects");
            release.countDown();
            core.get(5, TimeUnit.SECONDS);
            check("queued result".equals(queued.get(5, TimeUnit.SECONDS)), "Queued task completes");
            check("extra result".equals(extra.get(5, TimeUnit.SECONDS)), "Extra task completes");
        } finally {
            release.countDown();
            stop(pool);
        }
    }

    static void stagesAndCollections() throws Exception {
        var pool = Executors.newFixedThreadPool(2);
        try {
            var tenant = CompletableFuture.supplyAsync(() -> "tenant-a", pool);
            var connector = CompletableFuture.supplyAsync(() -> "crm", pool);
            var combined = tenant.thenCombine(connector, (tenantId, connectorId) -> tenantId + ":" + connectorId);
            var composed = combined.thenCompose(key -> CompletableFuture.completedFuture(key.length()));
            check(composed.get(5, TimeUnit.SECONDS) == 12, "Combine independent, compose dependent");
            var recovered = CompletableFuture.<String>failedFuture(new IllegalStateException("fixture"))
                    .handle((value, failure) -> failure == null ? value : "quarantined");
            check("quarantined".equals(recovered.get(5, TimeUnit.SECONDS)), "Explicit failure mapping");

            var counts = new ConcurrentHashMap<String, Integer>();
            Callable<Void> increment = () -> {
                for (int count = 0; count < 1000; count++) counts.merge("tenant-a", 1, Integer::sum);
                return null;
            };
            var first = pool.submit(increment);
            var second = pool.submit(increment);
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
            check(counts.get("tenant-a") == 2000, "Atomic per-key merge");
        } finally {
            stop(pool);
        }
    }

    static void interruptiblePermitWait() throws Exception {
        var permits = new Semaphore(0);
        var entered = new CountDownLatch(1);
        var exited = new CountDownLatch(1);
        var interrupted = new AtomicInteger();
        Thread waiter = Thread.ofVirtual().start(() -> {
            entered.countDown();
            try {
                permits.acquire();
            } catch (InterruptedException expected) {
                interrupted.incrementAndGet();
                Thread.currentThread().interrupt();
            } finally {
                exited.countDown();
            }
        });
        try {
            check(entered.await(5, TimeUnit.SECONDS), "Task entered permit path");
            waiter.interrupt();
            check(exited.await(5, TimeUnit.SECONDS), "Interrupt observed cooperatively");
            waiter.join(5000);
            check(!waiter.isAlive(), "Waiter terminated");
            check(interrupted.get() == 1, "Acquire aborted on interruption");
            check(permits.availablePermits() == 0, "No permit invented by cancelled wait");
        } finally {
            waiter.interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        new ConcurrencyLab().lostUpdateAndAtomics();
        monitorInvariant();
        queueThenGrowThenReject();
        stagesAndCollections();
        interruptiblePermitWait();
        System.out.println("ConcurrencyLab: all checks passed");
    }
}
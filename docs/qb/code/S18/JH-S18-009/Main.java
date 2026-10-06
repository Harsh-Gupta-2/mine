import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch release = new CountDownLatch(1), completed = new CountDownLatch(2);
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.AbortPolicy());
        pool.execute(() -> { try { release.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } finally { completed.countDown(); } });
        pool.execute(completed::countDown);
        boolean rejected = false;
        try { pool.execute(() -> {}); } catch (java.util.concurrent.RejectedExecutionException expected) { rejected = true; }
        if (!rejected) throw new AssertionError("queue should reject overflow");
        release.countDown(); pool.shutdown();
        if (!pool.awaitTermination(2, TimeUnit.SECONDS) || completed.getCount() != 0) throw new AssertionError("tasks incomplete");
        System.out.println("Bounded executor rejected overflow and completed accepted tasks.");
    }
}
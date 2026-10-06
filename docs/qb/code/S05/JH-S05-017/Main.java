import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// Demonstrates that submit() stores a task's exception in the Future while execute() lets it escape.
public class Main {
    public static void main(String[] args) throws Exception {
        Runnable boom = () -> {
            throw new IllegalStateException("task failed");
        };

        ExecutorService pool = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "worker");
            t.setUncaughtExceptionHandler((thread, ex) ->
                    System.out.println("uncaught handler saw: " + ex.getMessage()));
            return t;
        });

        System.out.println("--- submit, Future never read ---");
        pool.submit(boom);
        Thread.sleep(200);
        System.out.println("nothing was printed above");

        System.out.println("--- submit, Future read ---");
        Future<?> f = pool.submit(boom);
        Thread.sleep(200);
        try {
            f.get();
        } catch (Exception e) {
            System.out.println("get() threw: " + e.getClass().getSimpleName()
                    + " caused by " + e.getCause().getMessage());
        }

        System.out.println("--- execute ---");
        pool.execute(boom);
        Thread.sleep(200);

        pool.shutdown();
        pool.awaitTermination(1, TimeUnit.SECONDS);
    }
}

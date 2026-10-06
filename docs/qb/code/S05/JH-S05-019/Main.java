import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Shows thenApply vs thenCompose vs thenCombine, and that a failed stage skips downstream stages.
public class Main {
    public static void main(String[] args) throws Exception {
        ExecutorService io = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r, "io");
            t.setDaemon(true);
            return t;
        });

        CompletableFuture<Integer> base = CompletableFuture.supplyAsync(() -> 21, io);

        System.out.println("thenApply:   " + base.thenApply(v -> v * 2).join());

        System.out.println("thenCompose: "
                + base.thenCompose(v -> CompletableFuture.supplyAsync(() -> v + 1, io)).join());

        CompletableFuture<String> other = CompletableFuture.supplyAsync(() -> "answer", io);
        System.out.println("thenCombine: "
                + base.thenCombine(other, (n, s) -> s + "=" + n * 2).join());

        String recovered = CompletableFuture
                .<String>supplyAsync(() -> {
                    throw new IllegalStateException("downstream down");
                }, io)
                .thenApply(v -> {
                    System.out.println("this never runs");
                    return v.toUpperCase();
                })
                .exceptionally(ex -> "fallback after: " + ex.getCause().getMessage())
                .join();
        System.out.println("exceptionally: " + recovered);

        io.shutdown();
        io.awaitTermination(1, TimeUnit.SECONDS);
    }
}

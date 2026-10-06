import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> source = CompletableFuture.completedFuture("ok");
        IllegalStateException observerFailure = new IllegalStateException("observer failed");
        CompletableFuture<String> observed = source.whenComplete((value, failure) -> {
            throw observerFailure;
        });

        if (!"ok".equals(source.get())) {
            throw new AssertionError("Observer changed the source result");
        }
        System.out.println("source result: " + source.get());
        assertFailure(observed, observerFailure);
        System.out.println("dependent failure: " + observerFailure.getMessage());

        IllegalArgumentException originalFailure = new IllegalArgumentException("original failed");
        CompletableFuture<String> failedSource = CompletableFuture.failedFuture(originalFailure);
        CompletableFuture<String> failedObservation = failedSource.whenComplete((value, failure) -> {
            throw new IllegalStateException("second observer failed");
        });

        assertFailure(failedSource, originalFailure);
        assertFailure(failedObservation, originalFailure);
        System.out.println("failed source cause: " + originalFailure.getMessage());
        System.out.println("failed dependent cause: " + originalFailure.getMessage());
        System.out.println("All assertions passed.");
    }

    private static void assertFailure(CompletableFuture<?> future, Throwable expected)
            throws InterruptedException {
        if (!future.isDone()) {
            throw new AssertionError("Expected an already completed stage");
        }
        try {
            future.get();
            throw new AssertionError("Expected exceptional completion");
        } catch (ExecutionException failure) {
            if (failure.getCause() != expected) {
                throw new AssertionError("Unexpected failure cause", failure);
            }
        }
    }
}
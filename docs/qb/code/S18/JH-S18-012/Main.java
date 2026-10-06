import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class Main {
    static final class Registry<V> {
        private record Entry<V>(String request, V result) {}
        private final Map<String, Entry<V>> entries = new HashMap<>();
        synchronized V execute(String key, String request, Supplier<V> action) {
            Objects.requireNonNull(key); Objects.requireNonNull(request); Objects.requireNonNull(action);
            Entry<V> old = entries.get(key);
            if (old != null) {
                if (!old.request().equals(request)) throw new IllegalArgumentException("key reused for different request");
                return old.result();
            }
            V result = Objects.requireNonNull(action.get()); entries.put(key, new Entry<>(request, result)); return result;
        }
    }
    public static void main(String[] args) {
        Registry<String> registry = new Registry<>(); int[] calls = {0};
        String first = registry.execute("k1", "charge:500", () -> { calls[0]++; return "receipt-1"; });
        String replay = registry.execute("k1", "charge:500", () -> { calls[0]++; return "wrong"; });
        if (!first.equals(replay) || calls[0] != 1) throw new AssertionError("not idempotent");
        try { registry.execute("k1", "charge:700", () -> "bad"); throw new AssertionError("conflict accepted"); }
        catch (IllegalArgumentException expected) { System.out.println("Conflicting idempotency key rejected."); }
        System.out.println("Replay returned " + replay + "; action calls: " + calls[0]);
    }
}
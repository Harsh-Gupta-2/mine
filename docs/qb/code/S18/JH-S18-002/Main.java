import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.LongSupplier;

public class Main {
    static final class TtlCache<Key, Value> {
        private record Entry<Value>(Value value, long created) {}
        private final HashMap<Key, Entry<Value>> entries = new HashMap<>();
        private final LongSupplier clock;
        private final long ttlNanos;

        TtlCache(Duration ttl, LongSupplier clock) {
            ttlNanos = Objects.requireNonNull(ttl).toNanos();
            if (ttlNanos <= 0) throw new IllegalArgumentException("TTL must be positive");
            this.clock = Objects.requireNonNull(clock);
        }

        synchronized void put(Key key, Value value) {
            entries.put(Objects.requireNonNull(key), new Entry<>(Objects.requireNonNull(value), clock.getAsLong()));
        }

        synchronized Value get(Key key) {
            Entry<Value> entry = entries.get(Objects.requireNonNull(key));
            if (entry == null) return null;
            if (clock.getAsLong() - entry.created() >= ttlNanos) {
                entries.remove(key);
                return null;
            }
            return entry.value();
        }

        synchronized int purgeExpired() {
            long now = clock.getAsLong();
            int before = entries.size();
            entries.entrySet().removeIf(entry -> now - entry.getValue().created() >= ttlNanos);
            return before - entries.size();
        }
    }

    public static void main(String[] args) {
        AtomicLong ticks = new AtomicLong();
        TtlCache<String, String> cache = new TtlCache<>(Duration.ofNanos(10), ticks::get);
        cache.put("key", "first");
        ticks.set(9);
        if (!"first".equals(cache.get("key"))) throw new AssertionError("early expiry");
        ticks.set(10);
        if (cache.get("key") != null) throw new AssertionError("expiry boundary");
        cache.put("key", "second");
        ticks.set(19);
        if (!"second".equals(cache.get("key"))) throw new AssertionError("replacement TTL");
        ticks.set(20);
        if (cache.purgeExpired() != 1) throw new AssertionError("purge");
        if (cache.get("key") != null) throw new AssertionError("purged key");
        System.out.println("TTL boundary, replacement and purge assertions passed.");
    }
}
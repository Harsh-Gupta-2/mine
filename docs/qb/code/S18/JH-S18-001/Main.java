import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Main {
    static final class LruCache<Key, Value> {
        private final int capacity;
        private final LinkedHashMap<Key, Value> entries = new LinkedHashMap<>(16, 0.75f, true);

        LruCache(int capacity) {
            if (capacity < 1) throw new IllegalArgumentException("capacity must be positive");
            this.capacity = capacity;
        }

        synchronized Value get(Key key) {
            return entries.get(Objects.requireNonNull(key));
        }

        synchronized void put(Key key, Value value) {
            entries.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
            if (entries.size() > capacity) {
                var iterator = entries.keySet().iterator();
                iterator.next();
                iterator.remove();
            }
        }

        synchronized List<Key> keysOldestFirst() {
            return List.copyOf(entries.keySet());
        }
    }

    public static void main(String[] args) {
        LruCache<String, Integer> cache = new LruCache<>(2);
        cache.put("alpha", 1);
        cache.put("beta", 2);
        if (cache.get("alpha") != 1) throw new AssertionError("read");
        cache.put("gamma", 3);
        if (cache.get("beta") != null) throw new AssertionError("eviction");
        cache.put("alpha", 4);
        if (!cache.keysOldestFirst().equals(List.of("gamma", "alpha"))) throw new AssertionError("order");
        if (cache.get("alpha") != 4) throw new AssertionError("replacement");
        try {
            new LruCache<String, Integer>(0);
            throw new AssertionError("invalid capacity accepted");
        } catch (IllegalArgumentException expected) {
            System.out.println("Invalid capacity rejected.");
        }
        System.out.println("LRU keys: " + cache.keysOldestFirst());
        System.out.println("LRU assertions passed.");
    }
}
import java.util.function.LongSupplier;

public class Main {
    static final class TokenBucket {
        private final long capacity, refillTokens, refillPeriod;
        private final LongSupplier clock;
        private long tokens, lastRefill;
        TokenBucket(long capacity, long refillTokens, long refillPeriod, LongSupplier clock) {
            if (capacity < 1 || refillTokens < 1 || refillPeriod < 1) throw new IllegalArgumentException();
            this.capacity = capacity; this.refillTokens = refillTokens; this.refillPeriod = refillPeriod;
            this.clock = clock; tokens = capacity; lastRefill = clock.getAsLong();
        }
        synchronized boolean acquire() {
            long now = clock.getAsLong();
            if (now < lastRefill) throw new IllegalStateException("clock moved backwards");
            long periods = (now - lastRefill) / refillPeriod;
            if (periods > 0) {
                tokens = Math.min(capacity, tokens + Math.min(capacity, periods * Math.min(refillTokens, capacity)));
                lastRefill += periods * refillPeriod;
            }
            if (tokens == 0) return false;
            tokens--; return true;
        }
    }
    public static void main(String[] args) {
        long[] now = {0}; TokenBucket bucket = new TokenBucket(3, 1, 10, () -> now[0]);
        if (!bucket.acquire() || !bucket.acquire() || !bucket.acquire() || bucket.acquire()) throw new AssertionError("capacity");
        now[0] = 10;
        if (!bucket.acquire() || bucket.acquire()) throw new AssertionError("refill");
        System.out.println("Token bucket capacity and fake-clock refill assertions passed.");
    }
}
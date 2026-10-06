import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class LldLab {
    static void require(boolean condition, String message) {
        if (!condition) throw new IllegalArgumentException(message);
    }
    static void check(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
    static void rejects(Runnable action) {
        boolean rejected = false;
        try { action.run(); } catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "invalid operation rejected");
    }
    static final class Clock implements LongSupplier {
        private long now;
        public long getAsLong() { return now; }
        void advance(long amount) { require(amount >= 0, "nonnegative duration"); now = Math.addExact(now, amount); }
    }

    static final class LruCache<Key, Value> {
        private final int capacity;
        private final LinkedHashMap<Key, Value> entries = new LinkedHashMap<>(16, 0.75f, true);
        LruCache(int capacity) { require(capacity > 0, "positive capacity"); this.capacity = capacity; }
        synchronized Optional<Value> get(Key key) { return Optional.ofNullable(entries.get(Objects.requireNonNull(key))); }
        synchronized void put(Key key, Value value) {
            entries.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
            if (entries.size() > capacity) entries.remove(entries.keySet().iterator().next());
        }
        synchronized int size() { return entries.size(); }
    }

    static final class TokenBucket {
        private final double capacity;
        private final double perTick;
        private final LongSupplier clock;
        private double tokens;
        private long last;
        TokenBucket(int capacity, double perTick, LongSupplier clock) {
            require(capacity > 0 && Double.isFinite(perTick) && perTick > 0, "positive finite rates");
            this.capacity = capacity; this.perTick = perTick; this.clock = clock;
            tokens = capacity; last = clock.getAsLong();
        }
        synchronized boolean allow(int cost) {
            require(cost > 0 && cost <= capacity, "valid token cost");
            long now = clock.getAsLong();
            require(now >= last, "monotonic clock");
            tokens = Math.min(capacity, tokens + (now - last) * perTick); last = now;
            if (tokens < cost) return false;
            tokens -= cost; return true;
        }
    }

    enum Size { SMALL, LARGE }
    record Ticket(long id, String vehicle, int spot) { }
    static final class ParkingLot {
        private final List<Size> sizes;
        private final Map<Integer, Ticket> occupied = new HashMap<>();
        private final Map<String, Ticket> vehicles = new HashMap<>();
        private long sequence;
        ParkingLot(List<Size> sizes) { this.sizes = List.copyOf(sizes); require(!sizes.isEmpty(), "spots required"); }
        synchronized Optional<Ticket> park(String vehicle, Size size) {
            Objects.requireNonNull(vehicle); Objects.requireNonNull(size);
            require(!vehicles.containsKey(vehicle), "vehicle already parked");
            for (int spot = 0; spot < sizes.size(); spot++) {
                if (!occupied.containsKey(spot) && sizes.get(spot).ordinal() >= size.ordinal()) {
                    Ticket ticket = new Ticket(++sequence, vehicle, spot);
                    occupied.put(spot, ticket); vehicles.put(vehicle, ticket); return Optional.of(ticket);
                }
            }
            return Optional.empty();
        }
        synchronized boolean leave(Ticket ticket) {
            if (!ticket.equals(occupied.get(ticket.spot()))) return false;
            occupied.remove(ticket.spot()); vehicles.remove(ticket.vehicle()); return true;
        }
    }

    static final class Elevator {
        private final int top;
        private final TreeSet<Integer> stops = new TreeSet<>();
        private int floor;
        private boolean up = true;
        private boolean open;
        Elevator(int top) { require(top > 0, "positive top floor"); this.top = top; }
        synchronized void request(int destination) { require(destination >= 0 && destination <= top, "floor range"); stops.add(destination); }
        synchronized int step() {
            if (open) { open = false; return floor; }
            if (stops.remove(floor)) { open = true; return floor; }
            if (stops.isEmpty()) return floor;
            Integer target = up ? stops.higher(floor) : stops.lower(floor);
            if (target == null) { up = !up; target = up ? stops.higher(floor) : stops.lower(floor); }
            floor += Integer.compare(target, floor);
            if (stops.remove(floor)) open = true;
            return floor;
        }
        synchronized boolean doorOpen() { return open; }
    }

    enum NoticeState { QUEUED, SENDING, SENT }
    record Notice(String payload, NoticeState state) { }
    static final class Notifications {
        private final Map<String, Notice> notices = new HashMap<>();
        synchronized void submit(String id, String payload) {
            Objects.requireNonNull(id); Objects.requireNonNull(payload);
            Notice old = notices.putIfAbsent(id, new Notice(payload, NoticeState.QUEUED));
            require(old == null || old.payload().equals(payload), "identity/content conflict");
        }
        boolean deliver(String id, Consumer<String> sender) {
            Notice notice;
            synchronized (this) {
                notice = notices.get(id);
                require(notice != null, "unknown notice");
                if (notice.state() != NoticeState.QUEUED) return false;
                notices.put(id, new Notice(notice.payload(), NoticeState.SENDING));
            }
            boolean sent = false;
            try { sender.accept(notice.payload()); sent = true; return true; }
            finally {
                synchronized (this) { notices.put(id, new Notice(notice.payload(), sent ? NoticeState.SENT : NoticeState.QUEUED)); }
            }
        }
        synchronized NoticeState state(String id) { return notices.get(id).state(); }
    }

    enum Level { DEBUG, INFO, ERROR }
    record LogEvent(Level level, String message) { }
    static final class Logger {
        private final Level minimum;
        private final List<Consumer<LogEvent>> sinks;
        Logger(Level minimum, List<Consumer<LogEvent>> sinks) { this.minimum = minimum; this.sinks = List.copyOf(sinks); }
        int log(Level level, String message) {
            Objects.requireNonNull(level); Objects.requireNonNull(message);
            if (level.ordinal() < minimum.ordinal()) return 0;
            LogEvent event = new LogEvent(level, message);
            int failures = 0;
            for (Consumer<LogEvent> sink : sinks) {
                try { sink.accept(event); } catch (RuntimeException failure) { failures++; }
            }
            return failures;
        }
    }

    record Task(String id, long due, long order, Runnable action) { }
    static final class Scheduler {
        private final LongSupplier clock;
        private final Map<String, Task> pending = new HashMap<>();
        private final PriorityQueue<Task> queue = new PriorityQueue<>(java.util.Comparator.comparingLong(Task::due).thenComparingLong(Task::order));
        private long sequence;
        Scheduler(LongSupplier clock) { this.clock = clock; }
        synchronized void schedule(String id, long delay, Runnable action) {
            require(delay >= 0 && !pending.containsKey(id), "unique pending id and nonnegative delay");
            Task task = new Task(Objects.requireNonNull(id), Math.addExact(clock.getAsLong(), delay), ++sequence, Objects.requireNonNull(action));
            pending.put(id, task); queue.add(task);
        }
        synchronized boolean cancel(String id) {
            Task task = pending.remove(id); return task != null && queue.remove(task);
        }
        List<String> runDue() {
            List<Task> claimed = new ArrayList<>();
            synchronized (this) {
                long now = clock.getAsLong();
                while (!queue.isEmpty() && queue.peek().due() <= now) {
                    Task task = queue.remove(); pending.remove(task.id()); claimed.add(task);
                }
            }
            List<String> failures = new ArrayList<>();
            for (Task task : claimed) {
                try { task.action().run(); } catch (RuntimeException failure) { failures.add(task.id()); }
            }
            return List.copyOf(failures);
        }
    }

    record Expiring<Value>(Value value, long deadline) { }
    static final class TtlStore<Key, Value> {
        private final int capacity;
        private final LongSupplier clock;
        private final Map<Key, Expiring<Value>> values = new HashMap<>();
        TtlStore(int capacity, LongSupplier clock) { require(capacity > 0, "positive capacity"); this.capacity = capacity; this.clock = clock; }
        synchronized void put(Key key, Value value, long ttl) {
            Objects.requireNonNull(key); Objects.requireNonNull(value); require(ttl > 0, "positive TTL");
            long now = clock.getAsLong();
            values.entrySet().removeIf(entry -> entry.getValue().deadline() <= now);
            require(values.containsKey(key) || values.size() < capacity, "capacity exhausted");
            values.put(key, new Expiring<>(value, Math.addExact(now, ttl)));
        }
        synchronized Optional<Value> get(Key key) {
            Expiring<Value> entry = values.get(key);
            if (entry == null) return Optional.empty();
            if (clock.getAsLong() >= entry.deadline()) { values.remove(key); return Optional.empty(); }
            return Optional.of(entry.value());
        }
    }

    static final class EventBus {
        private final Map<String, LinkedHashMap<Long, Consumer<String>>> topics = new HashMap<>();
        private long sequence;
        synchronized long subscribe(String topic, Consumer<String> subscriber) {
            Objects.requireNonNull(topic); Objects.requireNonNull(subscriber);
            long id = ++sequence; topics.computeIfAbsent(topic, ignored -> new LinkedHashMap<>()).put(id, subscriber); return id;
        }
        synchronized boolean unsubscribe(String topic, long id) {
            Map<Long, Consumer<String>> subscribers = topics.get(topic);
            if (subscribers == null) return false;
            boolean removed = subscribers.remove(id) != null;
            if (subscribers.isEmpty()) topics.remove(topic);
            return removed;
        }
        int publish(String topic, String event) {
            List<Consumer<String>> snapshot;
            synchronized (this) { snapshot = List.copyOf(topics.getOrDefault(topic, new LinkedHashMap<>()).values()); }
            int failures = 0;
            for (Consumer<String> subscriber : snapshot) {
                try { subscriber.accept(event); } catch (RuntimeException failure) { failures++; }
            }
            return failures;
        }
    }

    interface Connector { String transform(String input); }
    static final class TransientFailure extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
    static final class RetryingConnector implements Connector {
        private final Connector delegate;
        private final int attempts;
        RetryingConnector(Connector delegate, int attempts) { require(attempts > 0, "positive attempts"); this.delegate = Objects.requireNonNull(delegate); this.attempts = attempts; }
        public String transform(String input) {
            for (int attempt = 1; ; attempt++) {
                try { return delegate.transform(input); }
                catch (TransientFailure failure) { if (attempt == attempts) throw failure; }
            }
        }
    }
    static final class Connectors {
        private final Map<String, Supplier<Connector>> factories;
        Connectors(Map<String, Supplier<Connector>> factories) { this.factories = Map.copyOf(factories); }
        Connector create(String type) {
            Supplier<Connector> factory = factories.get(type); require(factory != null, "unknown connector"); return factory.get();
        }
    }

    record RequestKey(String tenant, String operation, String key) { }
    record Pending(String content, CompletableFuture<String> result) { }
    static final class IdempotencyRegistry {
        private final int capacity;
        private final Map<RequestKey, Pending> requests = new HashMap<>();
        IdempotencyRegistry(int capacity) { require(capacity > 0, "positive capacity"); this.capacity = capacity; }
        String execute(RequestKey key, String content, Supplier<String> action) {
            Pending entry;
            boolean owner;
            synchronized (this) {
                entry = requests.get(key); owner = entry == null;
                if (owner) {
                    require(requests.size() < capacity, "registry full");
                    entry = new Pending(Objects.requireNonNull(content), new CompletableFuture<>()); requests.put(Objects.requireNonNull(key), entry);
                } else require(entry.content().equals(content), "key reused with different content");
            }
            if (owner) {
                try { entry.result().complete(Objects.requireNonNull(action.get())); }
                catch (RuntimeException | Error failure) {
                    entry.result().completeExceptionally(failure);
                    synchronized (this) { requests.remove(key, entry); }
                    throw failure;
                }
            }
            return entry.result().join();
        }
    }

    enum DeliveryResult { SUCCESS, RETRYABLE, PERMANENT, UNKNOWN }
    enum DeliveryState { PENDING, IN_FLIGHT, SENT, DEAD, REVIEW }
    record Delivery(String payload, int attempts, long due, DeliveryState state) { }
    interface WebhookSender { DeliveryResult send(String id, String payload); }
    static final class WebhookDispatcher {
        private final Map<String, Delivery> deliveries = new HashMap<>();
        private final LongSupplier clock;
        private final int maximum;
        WebhookDispatcher(LongSupplier clock, int maximum) { require(maximum > 0, "positive attempts"); this.clock = clock; this.maximum = maximum; }
        synchronized void enqueue(String id, String payload) {
            Objects.requireNonNull(id); Objects.requireNonNull(payload);
            Delivery old = deliveries.putIfAbsent(id, new Delivery(payload, 0, clock.getAsLong(), DeliveryState.PENDING));
            require(old == null || old.payload().equals(payload), "delivery identity conflict");
        }
        boolean attempt(String id, WebhookSender sender) {
            Delivery current;
            synchronized (this) {
                current = deliveries.get(id); require(current != null, "unknown delivery");
                if (current.state() != DeliveryState.PENDING || current.due() > clock.getAsLong()) return false;
                deliveries.put(id, new Delivery(current.payload(), current.attempts() + 1, current.due(), DeliveryState.IN_FLIGHT));
            }
            DeliveryResult result;
            try { result = Objects.requireNonNull(sender.send(id, current.payload())); }
            catch (RuntimeException failure) { result = DeliveryResult.UNKNOWN; }
            synchronized (this) {
                int attempts = current.attempts() + 1;
                DeliveryState state = switch (result) {
                    case SUCCESS -> DeliveryState.SENT;
                    case PERMANENT -> DeliveryState.DEAD;
                    case UNKNOWN -> DeliveryState.REVIEW;
                    case RETRYABLE -> attempts >= maximum ? DeliveryState.DEAD : DeliveryState.PENDING;
                };
                deliveries.put(id, new Delivery(current.payload(), attempts, Math.addExact(clock.getAsLong(), attempts), state));
            }
            return true;
        }
        synchronized DeliveryState state(String id) { return deliveries.get(id).state(); }
    }

    static void testLru() {
        LruCache<String, Integer> cache = new LruCache<>(2);
        cache.put("a", 1); cache.put("b", 2); check(cache.get("a").orElseThrow() == 1, "read hit");
        cache.put("c", 3); check(cache.get("b").isEmpty(), "least recent evicted");
        cache.put("a", 4); check(cache.size() == 2 && cache.get("a").orElseThrow() == 4, "replace does not grow");
        rejects(() -> new LruCache<>(0));
    }
    static void testRateLimiter() {
        Clock clock = new Clock(); TokenBucket bucket = new TokenBucket(2, 1, clock);
        check(bucket.allow(2) && !bucket.allow(1), "burst exhausted");
        clock.advance(1); check(bucket.allow(1), "refill"); clock.advance(100); check(bucket.allow(2) && !bucket.allow(1), "refill capped");
        rejects(() -> bucket.allow(3));
    }
    static void testParking() {
        ParkingLot lot = new ParkingLot(List.of(Size.SMALL, Size.LARGE));
        Ticket ticket = lot.park("van", Size.LARGE).orElseThrow(); check(ticket.spot() == 1, "large needs large");
        check(lot.park("bus", Size.LARGE).isEmpty(), "no oversized allocation");
        rejects(() -> lot.park("van", Size.LARGE));
        check(lot.leave(ticket) && !lot.leave(ticket), "ticket release once");
        check(lot.park("bus", Size.LARGE).isPresent(), "released capacity reusable");
    }
    static void testElevator() {
        Elevator elevator = new Elevator(5); elevator.request(2); elevator.request(1);
        check(elevator.step() == 1 && elevator.doorOpen(), "first stop opens door");
        check(elevator.step() == 1 && !elevator.doorOpen(), "door closes without moving");
        check(elevator.step() == 2 && elevator.doorOpen(), "next stop");
        elevator.request(0); elevator.step(); check(elevator.step() == 1, "reverse toward remaining lower stop");
        check(elevator.step() == 0 && elevator.doorOpen(), "arrive at ground"); rejects(() -> elevator.request(6));
    }
    static void testNotifications() {
        Notifications service = new Notifications(); service.submit("n1", "hello"); service.submit("n1", "hello");
        rejects(() -> service.submit("n1", "changed"));
        try { service.deliver("n1", payload -> { throw new IllegalStateException("temporary"); }); } catch (IllegalStateException expected) { }
        check(service.state("n1") == NoticeState.QUEUED, "failed attempt can retry");
        AtomicInteger sent = new AtomicInteger(); service.deliver("n1", payload -> sent.incrementAndGet());
        check(!service.deliver("n1", payload -> sent.incrementAndGet()) && sent.get() == 1, "completed notice not resent");
    }
    static void testLogger() {
        List<LogEvent> events = new ArrayList<>();
        Logger logger = new Logger(Level.INFO, List.of(event -> { throw new IllegalStateException("sink"); }, events::add));
        check(logger.log(Level.DEBUG, "hidden") == 0 && events.isEmpty(), "threshold filters");
        check(logger.log(Level.ERROR, "safe message") == 1 && events.size() == 1, "failure isolated across sinks");
    }
    static void testScheduler() {
        Clock clock = new Clock(); Scheduler scheduler = new Scheduler(clock); List<String> order = new ArrayList<>();
        scheduler.schedule("later", 2, () -> order.add("later")); scheduler.schedule("first", 1, () -> order.add("first"));
        scheduler.schedule("cancel", 1, () -> order.add("bad")); check(scheduler.cancel("cancel"), "cancel pending");
        scheduler.runDue(); check(order.isEmpty(), "not before deadline"); clock.advance(2); scheduler.runDue();
        check(order.equals(List.of("first", "later")), "due order"); scheduler.runDue(); check(order.size() == 2, "one shot");
        scheduler.schedule("failure", 0, () -> { throw new IllegalStateException("task"); });
        check(scheduler.runDue().equals(List.of("failure")), "failure reported");
    }
    static void testTtl() {
        Clock clock = new Clock(); TtlStore<String, String> store = new TtlStore<>(1, clock);
        store.put("a", "first", 2); check(store.get("a").orElseThrow().equals("first"), "before expiry");
        rejects(() -> store.put("b", "full", 1)); clock.advance(2); check(store.get("a").isEmpty(), "expires at boundary");
        store.put("a", "new", 3); clock.advance(2); check(store.get("a").isPresent(), "replacement has new expiry");
        clock.advance(1); check(store.get("a").isEmpty(), "replacement expires");
    }
    static void testPubSub() {
        EventBus bus = new EventBus(); List<String> seen = new ArrayList<>(); long subscription = bus.subscribe("jobs", seen::add);
        bus.subscribe("jobs", event -> { throw new IllegalStateException("subscriber"); });
        check(bus.publish("jobs", "one") == 1 && seen.equals(List.of("one")), "fanout failure isolated");
        check(bus.unsubscribe("jobs", subscription), "unsubscribe"); bus.publish("jobs", "two");
        check(seen.size() == 1 && bus.publish("other", "none") == 0, "topic isolation and unsubscribe");
    }
    static void testConnectors() {
        AtomicInteger attempts = new AtomicInteger();
        Connectors factories = new Connectors(Map.of("upper", () -> new RetryingConnector(input -> {
            if (attempts.incrementAndGet() == 1) throw new TransientFailure(); return input.toUpperCase(java.util.Locale.ROOT);
        }, 2)));
        check(factories.create("upper").transform("job").equals("JOB") && attempts.get() == 2, "strategy retry decorator");
        rejects(() -> factories.create("missing"));
        AtomicInteger permanent = new AtomicInteger();
        rejects(() -> new RetryingConnector(input -> { permanent.incrementAndGet(); throw new IllegalArgumentException("invalid"); }, 3).transform("bad"));
        check(permanent.get() == 1, "permanent errors are not retried");
    }
    static void testIdempotency() throws Exception {
        IdempotencyRegistry registry = new IdempotencyRegistry(2); RequestKey key = new RequestKey("tenant", "submit", "k1");
        AtomicInteger effects = new AtomicInteger(); CountDownLatch gate = new CountDownLatch(1);
        var workers = Executors.newFixedThreadPool(2);
        try {
            var first = workers.submit(() -> { gate.await(); return registry.execute(key, "body", () -> "job-" + effects.incrementAndGet()); });
            var second = workers.submit(() -> { gate.await(); return registry.execute(key, "body", () -> "job-" + effects.incrementAndGet()); });
            gate.countDown(); check(first.get(10, TimeUnit.SECONDS).equals(second.get(10, TimeUnit.SECONDS)), "same accepted result");
            check(effects.get() == 1, "concurrent duplicates share one owner"); rejects(() -> registry.execute(key, "changed", () -> "bad"));
            check(registry.execute(new RequestKey("other", "submit", "k1"), "body", () -> "other-job").equals("other-job"), "tenant scope");
            rejects(() -> registry.execute(new RequestKey("third", "submit", "k1"), "body", () -> "full"));
        } finally { workers.shutdownNow(); check(workers.awaitTermination(10, TimeUnit.SECONDS), "workers terminate"); }
    }
    static void testWebhooks() {
        Clock clock = new Clock(); WebhookDispatcher dispatcher = new WebhookDispatcher(clock, 2);
        dispatcher.enqueue("e1", "body"); dispatcher.attempt("e1", (id, payload) -> DeliveryResult.RETRYABLE);
        check(!dispatcher.attempt("e1", (id, payload) -> DeliveryResult.SUCCESS), "retry waits until due"); clock.advance(1);
        dispatcher.attempt("e1", (id, payload) -> DeliveryResult.SUCCESS); check(dispatcher.state("e1") == DeliveryState.SENT, "retry succeeds");
        check(!dispatcher.attempt("e1", (id, payload) -> DeliveryResult.SUCCESS), "sent not repeated");
        dispatcher.enqueue("e2", "body"); dispatcher.attempt("e2", (id, payload) -> DeliveryResult.UNKNOWN);
        check(dispatcher.state("e2") == DeliveryState.REVIEW, "unknown outcome not blindly retried");
        dispatcher.enqueue("e3", "body"); dispatcher.attempt("e3", (id, payload) -> DeliveryResult.RETRYABLE); clock.advance(1);
        dispatcher.attempt("e3", (id, payload) -> DeliveryResult.RETRYABLE); check(dispatcher.state("e3") == DeliveryState.DEAD, "attempt budget exhausted");
    }
    public static void main(String[] args) throws Exception {
        testLru(); testRateLimiter(); testParking(); testElevator(); testNotifications(); testLogger();
        testScheduler(); testTtl(); testPubSub(); testConnectors(); testIdempotency(); testWebhooks();
        System.out.println("Twelve LLD model checks completed");
    }
}
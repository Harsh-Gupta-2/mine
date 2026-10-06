import java.util.concurrent.CountDownLatch;

public class Main {
    static final class Account {
        final int id; int cents;
        Account(int id, int cents) { this.id = id; this.cents = cents; }
    }
    static void transfer(Account from, Account to, int cents) {
        if (cents < 0) throw new IllegalArgumentException("negative transfer");
        Account first = from.id < to.id ? from : to, second = from.id < to.id ? to : from;
        synchronized (first) { synchronized (second) {
            if (from.cents < cents) throw new IllegalStateException("insufficient funds");
            from.cents -= cents; to.cents += cents;
        }}
    }
    public static void main(String[] args) throws InterruptedException {
        Account a = new Account(1, 1_000), b = new Account(2, 1_000);
        CountDownLatch start = new CountDownLatch(1);
        Thread left = new Thread(() -> { try { start.await(); for (int i=0;i<500;i++) transfer(a,b,1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
        Thread right = new Thread(() -> { try { start.await(); for (int i=0;i<500;i++) transfer(b,a,1); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } });
        left.start(); right.start(); start.countDown(); left.join(2_000); right.join(2_000);
        if (left.isAlive() || right.isAlive()) throw new AssertionError("lock-order deadlock");
        if (a.cents + b.cents != 2_000) throw new AssertionError("money lost");
        try { transfer(a,b,-1); throw new AssertionError("negative accepted"); }
        catch (IllegalArgumentException expected) { System.out.println("Negative transfer rejected."); }
        System.out.println("Opposite-direction transfers finished; total cents: " + (a.cents + b.cents));
    }
}
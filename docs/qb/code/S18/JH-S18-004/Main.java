import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final int STOP = -1;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(2);
        int[] sum = {0};
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    int value = queue.take();
                    if (value == STOP) return;
                    sum[0] += value;
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        queue.put(2);
        queue.put(3);
        queue.put(STOP);
        consumer.join(2_000);
        if (consumer.isAlive()) throw new AssertionError("consumer did not finish");
        if (sum[0] != 5) throw new AssertionError("sum: " + sum[0]);
        if (queue.remainingCapacity() != 2) throw new AssertionError("queue not drained");
        System.out.println("BlockingQueue sum: " + sum[0]);
        System.out.println("Producer-consumer assertions passed.");
    }
}
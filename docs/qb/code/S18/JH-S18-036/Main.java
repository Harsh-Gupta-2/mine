import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Main {
    static final class KeyedSerialExecutor<K> {
        private final Executor executor;private final Map<K,CompletableFuture<Void>> tails=new HashMap<>();
        KeyedSerialExecutor(Executor executor){this.executor=executor;}
        synchronized CompletableFuture<Void> submit(K key,Runnable task){CompletableFuture<Void> previous=tails.getOrDefault(key,CompletableFuture.completedFuture(null));CompletableFuture<Void> next=previous.handle((ignored,failure)->null).thenRunAsync(task,executor);tails.put(key,next);next.whenComplete((ignored,failure)->{synchronized(this){tails.remove(key,next);}});return next;}
        synchronized int activeKeys(){return tails.size();}
    }
    public static void main(String[] args){var serial=new KeyedSerialExecutor<String>(Runnable::run);StringBuilder order=new StringBuilder();
        var one=serial.submit("a",()->order.append("1"));var two=serial.submit("a",()->order.append("2"));var other=serial.submit("b",()->order.append("b"));CompletableFuture.allOf(one,two,other).join();
        if(!order.toString().equals("12b")||serial.activeKeys()!=0)throw new AssertionError(order+" active keys="+serial.activeKeys());
        serial.submit("a",()->{throw new IllegalStateException("task failed");}).handle((v,e)->null).join();serial.submit("a",()->order.append("3")).join();
        if(!order.toString().equals("12b3"))throw new AssertionError("failed task blocked key");
        System.out.println("Per-key order preserved across task failure: "+order);}
}
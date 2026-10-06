import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class Main {
    static final class SingleFlight<K,V> {
        private final ConcurrentHashMap<K,CompletableFuture<V>> running=new ConcurrentHashMap<>();
        V get(K key,Function<K,V> loader){CompletableFuture<V> mine=new CompletableFuture<>();CompletableFuture<V> existing=running.putIfAbsent(key,mine);
            if(existing==null){try{mine.complete(loader.apply(key));}catch(Throwable failure){mine.completeExceptionally(failure);}finally{running.remove(key,mine);}existing=mine;}
            return existing.join();}
    }
    public static void main(String[] args)throws Exception{SingleFlight<String,String> flight=new SingleFlight<>();AtomicInteger loads=new AtomicInteger();CountDownLatch start=new CountDownLatch(1);ExecutorService pool=Executors.newFixedThreadPool(2);
        var a=pool.submit(()->{start.await();return flight.get("k",k->{loads.incrementAndGet();return "value";});});var b=pool.submit(()->{start.await();return flight.get("k",k->{loads.incrementAndGet();return "value";});});start.countDown();
        if(!"value".equals(a.get(2,TimeUnit.SECONDS))||!"value".equals(b.get(2,TimeUnit.SECONDS))||loads.get()!=1)throw new AssertionError("not coalesced");pool.shutdown();
        try{flight.get("bad",k->{throw new IllegalStateException("load failed");});throw new AssertionError("failure lost");}catch(java.util.concurrent.CompletionException expected){System.out.println("Loader failure propagated.");}
        if(!"recovered".equals(flight.get("bad",k->"recovered")))throw new AssertionError("failed entry retained");
        System.out.println("Concurrent requests shared one load; calls: "+loads.get());}
}
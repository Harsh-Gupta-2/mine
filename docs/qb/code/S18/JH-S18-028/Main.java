import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class Main {
    static final class Counters {
        private final ConcurrentHashMap<String,LongAdder> values=new ConcurrentHashMap<>();
        void increment(String key){values.computeIfAbsent(key,k->new LongAdder()).increment();}
        long get(String key){LongAdder value=values.get(key);return value==null?0:value.sum();}
        Map<String,Long> snapshot(){return values.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(Map.Entry::getKey,e->e.getValue().sum()));}
    }
    public static void main(String[] args)throws Exception{Counters counters=new Counters();int workers=4,each=1_000;CountDownLatch start=new CountDownLatch(1),done=new CountDownLatch(workers);ExecutorService pool=Executors.newFixedThreadPool(workers);
        for(int i=0;i<workers;i++)pool.execute(()->{try{start.await();for(int j=0;j<each;j++)counters.increment("ok");}catch(InterruptedException e){Thread.currentThread().interrupt();}finally{done.countDown();}});start.countDown();
        if(!done.await(2,TimeUnit.SECONDS))throw new AssertionError("workers stuck");pool.shutdown();if(counters.get("ok")!=workers*each||counters.get("missing")!=0)throw new AssertionError(counters.snapshot());
        System.out.println("Concurrent keyed count: "+counters.get("ok"));}
}
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Main {
    static final class Catalog {
        private final ReentrantReadWriteLock lock=new ReentrantReadWriteLock();private final Map<String,Integer> items=new HashMap<>();
        void put(String key,int value){lock.writeLock().lock();try{items.put(key,value);}finally{lock.writeLock().unlock();}}
        Integer get(String key){lock.readLock().lock();try{return items.get(key);}finally{lock.readLock().unlock();}}
        Map<String,Integer> snapshot(){lock.readLock().lock();try{return Map.copyOf(items);}finally{lock.readLock().unlock();}}
    }
    public static void main(String[] args){Catalog catalog=new Catalog();catalog.put("a",1);Map<String,Integer> old=catalog.snapshot();catalog.put("b",2);
        if(!old.equals(Map.of("a",1))||!catalog.snapshot().equals(Map.of("a",1,"b",2))||catalog.get("missing")!=null)throw new AssertionError("snapshot");
        System.out.println("Consistent catalog snapshot: "+new java.util.TreeMap<>(catalog.snapshot()));}
}
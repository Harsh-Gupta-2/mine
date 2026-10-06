import java.util.HashMap;
import java.util.Map;

public class Main {
    record Versioned<V>(long version,V value) {}
    static final class Store<K,V> {
        private final Map<K,Versioned<V>> rows=new HashMap<>();
        synchronized Versioned<V> read(K key){return rows.get(key);}
        synchronized boolean update(K key,long expected,V value){Versioned<V> old=rows.get(key);if(old==null||old.version()!=expected)return false;rows.put(key,new Versioned<>(Math.incrementExact(expected),value));return true;}
        synchronized void insert(K key,V value){if(rows.putIfAbsent(key,new Versioned<>(1,value))!=null)throw new IllegalStateException("exists");}
    }
    public static void main(String[] args){Store<String,String> store=new Store<>();store.insert("doc","v1");
        if(!store.update("doc",1,"v2")||store.update("doc",1,"lost"))throw new AssertionError("version conflict");
        if(store.read("doc").version()!=2||!store.read("doc").value().equals("v2")||store.update("missing",1,"x"))throw new AssertionError("read/missing");
        try{store.insert("doc","duplicate");throw new AssertionError("duplicate insert");}catch(IllegalStateException expected){System.out.println("Duplicate insert rejected.");}
        System.out.println("Stored version after stale-write rejection: "+store.read("doc"));}
}
import java.util.HashMap;
import java.util.Map;

public class Main {
    static final class Inventory {
        private final Map<String,Integer> onHand=new HashMap<>(),reserved=new HashMap<>();
        Inventory(Map<String,Integer> initial){for(var entry:initial.entrySet())if(entry.getKey()==null||entry.getKey().isBlank()||entry.getValue()==null||entry.getValue()<0)throw new IllegalArgumentException("invalid initial stock");onHand.putAll(initial);}
        synchronized void reserve(String sku,int quantity){if(quantity<1)throw new IllegalArgumentException();int available=onHand.getOrDefault(sku,0)-reserved.getOrDefault(sku,0);if(available<quantity)throw new IllegalStateException("insufficient stock");reserved.merge(sku,quantity,Math::addExact);}
        synchronized void release(String sku,int quantity){int held=reserved.getOrDefault(sku,0);if(quantity<1||quantity>held)throw new IllegalArgumentException("invalid release");if(quantity==held)reserved.remove(sku);else reserved.put(sku,held-quantity);}
        synchronized int available(String sku){return onHand.getOrDefault(sku,0)-reserved.getOrDefault(sku,0);}
    }
    public static void main(String[] args){Inventory stock=new Inventory(Map.of("book",5));stock.reserve("book",3);
        if(stock.available("book")!=2)throw new AssertionError("reservation");try{stock.reserve("book",3);throw new AssertionError("oversell");}catch(IllegalStateException expected){System.out.println("Oversell rejected without changing stock.");}
        stock.release("book",2);if(stock.available("book")!=4)throw new AssertionError("release");
        try{stock.release("book",2);throw new AssertionError("over-release");}catch(IllegalArgumentException expected){System.out.println("Over-release rejected.");}
        try{new Inventory(Map.of("book",-1));throw new AssertionError("negative initial stock");}catch(IllegalArgumentException expected){System.out.println("Negative initial stock rejected.");}
        System.out.println("Available book units: "+stock.available("book"));}
}
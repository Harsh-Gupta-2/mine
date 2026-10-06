import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Main {
    record Page(List<String> items,Integer nextAfter) {}
    static Page page(NavigableMap<Integer,String> rows,Integer after,int size){if(size<1)throw new IllegalArgumentException("page size");NavigableMap<Integer,String> remaining=after==null?rows:rows.tailMap(after,false);var iterator=remaining.entrySet().iterator();java.util.ArrayList<String> items=new java.util.ArrayList<>();Integer last=null;while(iterator.hasNext()&&items.size()<size){var e=iterator.next();items.add(e.getValue());last=e.getKey();}return new Page(List.copyOf(items),remaining.higherKey(last==null?Integer.MIN_VALUE:last)==null?null:last);}
    public static void main(String[] args){NavigableMap<Integer,String> rows=new TreeMap<>();rows.put(10,"a");rows.put(20,"b");rows.put(30,"c");
        Page first=page(rows,null,2),second=page(rows,first.nextAfter(),2);if(!first.items().equals(List.of("a","b"))||!second.items().equals(List.of("c"))||second.nextAfter()!=null)throw new AssertionError("pages");
        try{page(rows,null,0);throw new AssertionError("zero page");}catch(IllegalArgumentException expected){System.out.println("Invalid page size rejected.");}
        System.out.println("Stable keyset pages: "+first.items()+" then "+second.items());}
}
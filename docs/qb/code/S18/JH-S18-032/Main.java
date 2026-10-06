import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Main {
    record Log(long time,int source,int offset,String message) {}
    static List<Log> merge(List<List<Log>> feeds){PriorityQueue<Log> heap=new PriorityQueue<>(Comparator.comparingLong(Log::time).thenComparingInt(Log::source));
        int[] positions=new int[feeds.size()];for(int i=0;i<feeds.size();i++){if(!feeds.get(i).isEmpty())heap.add(feeds.get(i).get(0));}
        List<Log> result=new ArrayList<>();while(!heap.isEmpty()){Log next=heap.remove();result.add(next);int p=++positions[next.source()];if(p<feeds.get(next.source()).size())heap.add(feeds.get(next.source()).get(p));}return List.copyOf(result);}
    public static void main(String[] args){List<Log> merged=merge(List.of(List.of(new Log(1,0,0,"a"),new Log(4,0,1,"d")),List.of(new Log(2,1,0,"b"),new Log(4,1,1,"e"))));
        if(!merged.stream().map(Log::message).toList().equals(List.of("a","b","d","e")))throw new AssertionError(merged);
        if(!merge(List.of(List.of())).isEmpty())throw new AssertionError("empty feeds");
        System.out.println("Stable merged feed: "+merged.stream().map(Log::message).toList());}
}
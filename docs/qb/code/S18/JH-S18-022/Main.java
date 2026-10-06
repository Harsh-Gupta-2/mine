import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    static final class LatencyWindow {
        private final int capacity;private final ArrayDeque<Long> samples=new ArrayDeque<>();
        LatencyWindow(int capacity){if(capacity<1)throw new IllegalArgumentException();this.capacity=capacity;}
        void add(long millis){if(millis<0)throw new IllegalArgumentException("negative latency");if(samples.size()==capacity)samples.removeFirst();samples.addLast(millis);}
        long percentile(double p){if(samples.isEmpty()||!Double.isFinite(p)||p<=0||p>1)throw new IllegalArgumentException("invalid percentile/input");List<Long> sorted=new ArrayList<>(samples);Collections.sort(sorted);return sorted.get((int)Math.ceil(p*sorted.size())-1);}
        int size(){return samples.size();}
    }
    public static void main(String[] args){LatencyWindow w=new LatencyWindow(3);w.add(40);w.add(10);w.add(30);
        if(w.percentile(.95)!=40||w.percentile(.50)!=30)throw new AssertionError("percentiles");w.add(20);
        if(w.size()!=3||w.percentile(1)!=30)throw new AssertionError("ring eviction");
        try{w.add(-1);throw new AssertionError("negative latency");}catch(IllegalArgumentException expected){System.out.println("Negative latency rejected.");}
        try{w.percentile(Double.NaN);throw new AssertionError("NaN percentile");}catch(IllegalArgumentException expected){System.out.println("Non-finite percentile rejected.");}
        System.out.println("Rolling p95 after eviction: "+w.percentile(.95)+" ms");}
}
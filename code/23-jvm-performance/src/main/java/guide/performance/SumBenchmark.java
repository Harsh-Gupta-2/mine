package guide.performance;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 3, time = 1)
@Fork(2)
public class SumBenchmark {
    @Param({"1024", "16384"})
    public int size;
    private long[] values;

    @Setup
    public void setup() {
        Random random = new Random(42);
        values = new long[size];
        for (int index = 0; index < values.length; index++) values[index] = random.nextInt(1000);
        if (indexedSum() != streamSum()) throw new AssertionError("different results");
    }

    @Benchmark
    public long indexedSum() {
        long total = 0;
        for (long value : values) total += value;
        return total;
    }

    @Benchmark
    public long streamSum() { return Arrays.stream(values).sum(); }
}
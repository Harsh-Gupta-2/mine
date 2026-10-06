import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import jdk.jfr.Recording;

public final class ProfilingLab {
    public static void main(String[] args) throws Exception {
        Path destination = args.length == 0 ? Path.of("out", "guide-profile.jfr") : Path.of(args[0]);
        if (destination.getParent() != null) Files.createDirectories(destination.getParent());
        long checksum = 0;
        byte[][] retained = new byte[64][];
        try (Recording recording = new Recording()) {
            recording.setName("guide-bounded-fixture");
            recording.setMaxSize(16L * 1024 * 1024);
            recording.setMaxAge(Duration.ofSeconds(10));
            recording.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(20));
            recording.enable("jdk.ObjectAllocationSample");
            recording.enable("jdk.GarbageCollection");
            for (int warmup = 0; warmup < retained.length; warmup++) retained[warmup] = new byte[512];
            recording.start();
            for (int iteration = 0; iteration < 100_000; iteration++) {
                byte[] value = new byte[512];
                value[0] = (byte) iteration;
                retained[iteration % retained.length] = value;
                checksum += value[0];
            }
            recording.stop();
            recording.dump(destination);
        }
        if (Files.size(destination) == 0) throw new AssertionError("recording file is empty");
        System.out.println("Fixture recording created; checksum=" + checksum + ", retained slots=" + retained.length);
    }
}
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    record Parsed(String level, String service, String message) {}
    static Parsed parse(String line) {
        String[] fields = line.split("\\|", 3);
        if (fields.length != 3 || fields[0].isBlank() || fields[1].isBlank()) throw new IllegalArgumentException("bad log line");
        return new Parsed(fields[0], fields[1], fields[2]);
    }
    public static void main(String[] args) {
        List<String> lines = List.of("ERROR|billing|timeout", "INFO|auth|started", "ERROR|auth|denied", "ERROR|billing|retry");
        List<Parsed> parsed = lines.stream().map(Main::parse).toList();
        Map<String, Long> counts = parsed.stream().filter(row -> row.level().equals("ERROR"))
                .collect(Collectors.groupingBy(Parsed::service, TreeMap::new, Collectors.counting()));
        if (!counts.equals(Map.of("auth", 1L, "billing", 2L))) throw new AssertionError(counts);
        try { parse("ERROR|missing-message"); throw new AssertionError("malformed accepted"); }
        catch (IllegalArgumentException expected) { System.out.println("Malformed log line rejected."); }
        System.out.println("Error counts: " + counts);
        System.out.println("Log parsing assertions passed.");
    }
}
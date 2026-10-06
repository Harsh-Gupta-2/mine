import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<String> words = List.of("java", "spring", "java", "kafka", "spring", "java");

        Map<String, Long> byStream = words.stream()
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting()));
        System.out.println("groupingBy + counting: " + byStream);

        Map<String, Integer> byMerge = new TreeMap<>();
        for (String word : words) byMerge.merge(word, 1, Integer::sum);
        System.out.println("loop with merge:       " + byMerge);

        Map<String, Long> byCountDesc = byStream.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        System.out.println("sorted by count desc:  " + byCountDesc);

        List<String> sentences = List.of("Java and Spring", "java and Kafka");
        Map<String, Long> wordCount = sentences.stream()
                .flatMap(s -> Arrays.stream(s.split("\\s+")))
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(w -> w, TreeMap::new, Collectors.counting()));
        System.out.println("word count from lines: " + wordCount);
    }
}

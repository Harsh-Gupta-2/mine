import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static List<List<String>> bySortedKey(String[] words) {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        for (String w : words) {
            char[] chars = w.toCharArray();
            Arrays.sort(chars);
            groups.computeIfAbsent(new String(chars), k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(groups.values());
    }

    static List<List<String>> byCountKey(String[] words) {
        Map<String, List<String>> groups = new LinkedHashMap<>();
        for (String w : words) {
            int[] counts = new int[26];
            for (char ch : w.toCharArray()) counts[ch - 'a']++;
            StringBuilder key = new StringBuilder();
            for (int c : counts) key.append('#').append(c);
            groups.computeIfAbsent(key.toString(), k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(groups.values());
    }

    public static void main(String[] args) {
        String[] words = {"eat", "tea", "tan", "ate", "nat", "bat"};
        System.out.println("sorted-string key: " + bySortedKey(words));
        System.out.println("letter-count key:  " + byCountKey(words));
    }
}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    static void permuteBySwap(char[] c, int left, List<String> out) {
        if (left == c.length - 1) {
            out.add(new String(c));
            return;
        }
        for (int i = left; i < c.length; i++) {
            swap(c, left, i);
            permuteBySwap(c, left + 1, out);
            swap(c, left, i);
        }
    }

    static void permuteUnique(char[] sorted, boolean[] used, StringBuilder cur, List<String> out) {
        if (cur.length() == sorted.length) {
            out.add(cur.toString());
            return;
        }
        for (int i = 0; i < sorted.length; i++) {
            if (used[i]) continue;
            if (i > 0 && sorted[i] == sorted[i - 1] && !used[i - 1]) continue;
            used[i] = true;
            cur.append(sorted[i]);
            permuteUnique(sorted, used, cur, out);
            cur.deleteCharAt(cur.length() - 1);
            used[i] = false;
        }
    }

    private static void swap(char[] c, int i, int j) {
        char t = c[i];
        c[i] = c[j];
        c[j] = t;
    }

    public static void main(String[] args) {
        List<String> swapped = new ArrayList<>();
        permuteBySwap("abc".toCharArray(), 0, swapped);
        System.out.println("abc by swapping (" + swapped.size() + "): " + swapped);

        char[] aab = "aab".toCharArray();
        Arrays.sort(aab);
        List<String> unique = new ArrayList<>();
        permuteUnique(aab, new boolean[aab.length], new StringBuilder(), unique);
        System.out.println("aab unique (" + unique.size() + "): " + unique);
    }
}

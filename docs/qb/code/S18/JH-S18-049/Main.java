import java.util.HashSet;
import java.util.Set;

public class Main {
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        int best = 0;
        for (int n : set) {
            if (set.contains(n - 1)) continue;
            int current = n, length = 1;
            while (set.contains(current + 1)) {
                current++;
                length++;
            }
            best = Math.max(best, length);
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("[100,4,200,1,3,2] -> " + longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
        System.out.println("[0,3,7,2,5,8,4,6,0,1] -> " + longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}));
        System.out.println("[] -> " + longestConsecutive(new int[]{}));
    }
}

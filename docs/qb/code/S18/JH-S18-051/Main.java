import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static boolean subsetSumExists(int[] a, int target) {
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        for (int num : a) {
            for (int t = target; t >= num; t--) dp[t] |= dp[t - num];
        }
        return dp[target];
    }

    static void allSubsets(int[] a, int index, int remaining, List<Integer> current, List<List<Integer>> out) {
        if (remaining == 0) {
            out.add(new ArrayList<>(current));
            return;
        }
        if (index == a.length || remaining < 0) return;
        current.add(a[index]);
        allSubsets(a, index + 1, remaining - a[index], current, out);
        current.remove(current.size() - 1);
        allSubsets(a, index + 1, remaining, current, out);
    }

    static int[] twoSum(int[] a, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            Integer j = seen.get(target - a[i]);
            if (j != null) return new int[]{j, i};
            seen.put(a[i], i);
        }
        return new int[0];
    }

    public static void main(String[] args) {
        int[] a = {3, 34, 4, 12, 5, 2};
        System.out.println("any subset sums to 9: " + subsetSumExists(a, 9));
        System.out.println("any subset sums to 30: " + subsetSumExists(a, 30));
        List<List<Integer>> subsets = new ArrayList<>();
        allSubsets(a, 0, 9, new ArrayList<>(), subsets);
        System.out.println("all subsets that sum to 9: " + subsets);
        System.out.println("two-sum indices in [2,7,11,15] for 9: " + Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)));
    }
}

import java.util.HashMap;
import java.util.Map;

public class Main {
    static int maxSumOfSizeK(int[] a, int k) {
        int sum = 0;
        for (int i = 0; i < k; i++) sum += a[i];
        int best = sum;
        for (int i = k; i < a.length; i++) {
            sum += a[i] - a[i - k];
            best = Math.max(best, sum);
        }
        return best;
    }

    static int longestWithoutRepeat(String s) {
        Map<Character, Integer> last = new HashMap<>();
        int left = 0, best = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (last.containsKey(c)) left = Math.max(left, last.get(c) + 1);
            last.put(c, right);
            best = Math.max(best, right - left + 1);
        }
        return best;
    }

    static int shortestSubarrayAtLeast(int[] a, int target) {
        int left = 0, sum = 0, best = Integer.MAX_VALUE;
        for (int right = 0; right < a.length; right++) {
            sum += a[right];
            while (sum >= target) {
                best = Math.min(best, right - left + 1);
                sum -= a[left++];
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    public static void main(String[] args) {
        System.out.println("fixed window, max sum of size 3 in [5,3,1,4,7,9]: " + maxSumOfSizeK(new int[]{5, 3, 1, 4, 7, 9}, 3));
        System.out.println("dynamic window, longest unique substring 'abcabcbb': " + longestWithoutRepeat("abcabcbb"));
        System.out.println("dynamic window, longest unique substring 'pwwkew': " + longestWithoutRepeat("pwwkew"));
        System.out.println("dynamic window, shortest subarray with sum >= 7 in [2,3,1,2,4,3]: "
                + shortestSubarrayAtLeast(new int[]{2, 3, 1, 2, 4, 3}, 7));
    }
}

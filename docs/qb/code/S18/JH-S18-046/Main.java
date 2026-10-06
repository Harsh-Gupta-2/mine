public class Main {
    static int duplicateBySum(int[] a) {
        int n = a.length - 1;
        long expected = (long) n * (n + 1) / 2;
        long actual = 0;
        for (int x : a) actual += x;
        return (int) (actual - expected);
    }

    static int duplicateByFloyd(int[] a) {
        int slow = a[0], fast = a[0];
        do {
            slow = a[slow];
            fast = a[a[fast]];
        } while (slow != fast);
        slow = a[0];
        while (slow != fast) {
            slow = a[slow];
            fast = a[fast];
        }
        return slow;
    }

    static int missingByXor(int[] a, int n) {
        int xor = 0;
        for (int i = 1; i <= n; i++) xor ^= i;
        for (int x : a) xor ^= x;
        return xor;
    }

    public static void main(String[] args) {
        System.out.println("sum formula on {1,2,3,3,4,5}: " + duplicateBySum(new int[]{1, 2, 3, 3, 4, 5}));
        System.out.println("Floyd on {1,3,4,2,2}:         " + duplicateByFloyd(new int[]{1, 3, 4, 2, 2}));
        System.out.println("Floyd on {3,1,3,4,2}:         " + duplicateByFloyd(new int[]{3, 1, 3, 4, 2}));
        System.out.println("XOR missing in {1,2,4,5}, n=5: " + missingByXor(new int[]{1, 2, 4, 5}, 5));
    }
}

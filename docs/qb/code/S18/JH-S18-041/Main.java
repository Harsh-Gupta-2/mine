import java.util.Objects;

public class Main {
    static final class MyHashMap<K, V> {
        private static final class Node<K, V> {
            final int hash;
            final K key;
            V value;
            Node<K, V> next;

            Node(int hash, K key, V value, Node<K, V> next) {
                this.hash = hash;
                this.key = key;
                this.value = value;
                this.next = next;
            }
        }

        private static final float LOAD_FACTOR = 0.75f;
        private Node<K, V>[] table;
        private int size;

        @SuppressWarnings("unchecked")
        MyHashMap() {
            table = (Node<K, V>[]) new Node[16];
        }

        private static int spread(Object key) {
            int h = key == null ? 0 : key.hashCode();
            return h ^ (h >>> 16);
        }

        private static int index(int hash, int length) {
            return (length - 1) & hash;
        }

        V put(K key, V value) {
            int hash = spread(key);
            int i = index(hash, table.length);
            for (Node<K, V> n = table[i]; n != null; n = n.next) {
                if (n.hash == hash && Objects.equals(n.key, key)) {
                    V old = n.value;
                    n.value = value;
                    return old;
                }
            }
            table[i] = new Node<>(hash, key, value, table[i]);
            if (++size > table.length * LOAD_FACTOR) resize();
            return null;
        }

        V get(K key) {
            int hash = spread(key);
            for (Node<K, V> n = table[index(hash, table.length)]; n != null; n = n.next) {
                if (n.hash == hash && Objects.equals(n.key, key)) return n.value;
            }
            return null;
        }

        V remove(K key) {
            int hash = spread(key);
            int i = index(hash, table.length);
            Node<K, V> prev = null;
            for (Node<K, V> n = table[i]; n != null; prev = n, n = n.next) {
                if (n.hash == hash && Objects.equals(n.key, key)) {
                    if (prev == null) table[i] = n.next; else prev.next = n.next;
                    size--;
                    return n.value;
                }
            }
            return null;
        }

        int size() { return size; }

        int capacity() { return table.length; }

        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] bigger = (Node<K, V>[]) new Node[table.length * 2];
            for (Node<K, V> head : table) {
                for (Node<K, V> n = head; n != null; ) {
                    Node<K, V> next = n.next;
                    int i = index(n.hash, bigger.length);
                    n.next = bigger[i];
                    bigger[i] = n;
                    n = next;
                }
            }
            table = bigger;
        }
    }

    record BadKey(int id) {
        @Override public int hashCode() { return 1; }
    }

    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        System.out.println("put existing key returns old value: " + map.put("a", 10));
        System.out.println("get a = " + map.get("a") + ", get b = " + map.get("b") + ", get missing = " + map.get("zzz"));
        map.put(null, 99);
        System.out.println("null key = " + map.get(null));
        System.out.println("remove b = " + map.remove("b") + ", size = " + map.size());
        for (int i = 0; i < 20; i++) map.put("k" + i, i);
        System.out.println("size after 20 more inserts = " + map.size() + ", capacity = " + map.capacity());
        MyHashMap<BadKey, String> collide = new MyHashMap<>();
        for (int i = 0; i < 5; i++) collide.put(new BadKey(i), "v" + i);
        System.out.println("five keys sharing one hash all retrievable: "
                + (collide.get(new BadKey(3)).equals("v3") && collide.size() == 5));
    }
}

package mama.pluto.utils;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DoubleMap<K1, K2> implements Iterable<Pair<K1, K2>> {

    private final Map<K1, K2> k1s;
    private final Map<K2, K1> k2s;

    public DoubleMap() {
        this.k1s = new HashMap<>();
        this.k2s = new HashMap<>();
    }

    public DoubleMap(int initialCapacity) {
        this.k1s = new HashMap<>(initialCapacity);
        this.k2s = new HashMap<>(initialCapacity);
    }

    private DoubleMap(Map<K1, K2> k1s, Map<K2, K1> k2s) {
        this.k1s = k1s;
        this.k2s = k2s;
    }


    public static <K1, K2> DoubleMap<K1, K2> getInstanceFromMap(Map<K1, K2> fromMap) {
        Map<K1, K2> k1s = new HashMap<>(fromMap);
        Map<K2, K1> k2s = new HashMap<>(fromMap.size());
        for (Map.Entry<K1, K2> k1K2Entry : fromMap.entrySet()) {
            k2s.put(k1K2Entry.getValue(), k1K2Entry.getKey());
        }
        return new DoubleMap<>(k1s, k2s);
    }

    public static <K1, K2> DoubleMap<K1, K2> getInstanceFromReversedMap(Map<K2, K1> fromMap) {
        Map<K1, K2> k1s = new HashMap<>(fromMap.size());
        Map<K2, K1> k2s = new HashMap<>(fromMap);
        for (Map.Entry<K2, K1> k1K2Entry : fromMap.entrySet()) {
            k1s.put(k1K2Entry.getValue(), k1K2Entry.getKey());
        }
        return new DoubleMap<>(k1s, k2s);
    }

    public boolean put(final K1 k1, final K2 k2) {
        if (k1s.containsKey(k1) || k2s.containsKey(k2)) {
            return false;
        } else {
            k1s.put(k1, k2);
            k2s.put(k2, k1);
            return true;
        }
    }

    public boolean containsK1(K1 k1) {
        return k1s.containsKey(k1);
    }

    public boolean containsK2(K2 k2) {
        return k2s.containsKey(k2);
    }

    public K1 getK1(K2 k2) {
        return k2s.get(k2);
    }

    public K2 getK2(K1 k1) {
        return k1s.get(k1);
    }

    public int size() {
        return k1s.size();
    }

    @NonNull
    @Override
    public Iterator<Pair<K1, K2>> iterator() {
        final Iterator<K1> k1Iter = k1s.keySet().iterator();
        final Iterator<K2> k2Iter = k2s.keySet().iterator();
        return new Iterator<Pair<K1, K2>>() {

            @Override
            public boolean hasNext() {
                return k1Iter.hasNext() && k2Iter.hasNext();
            }

            @Override
            public Pair<K1, K2> next() {
                return new Pair<>(k1Iter.next(), k2Iter.next());
            }
        };
    }
}

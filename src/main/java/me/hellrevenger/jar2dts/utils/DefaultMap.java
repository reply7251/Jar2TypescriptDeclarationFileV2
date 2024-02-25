package me.hellrevenger.jar2dts.utils;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Supplier;

public class DefaultMap<K, V> extends TreeMap<K, V> {
    public V getOrCreate(K key, Supplier<V> constructor) {
        V namespace = get(key);
        if(namespace == null) {
            namespace = constructor.get();
            put(key, namespace);
        }
        return namespace;
    }
}

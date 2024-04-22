package com.superkele.translation.core.util;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Objects;

public class Pair<K, V>  implements Serializable {
    private static final long serialVersionUID = 1L;
    protected K key;
    protected V value;

    public static <K, V> Pair<Object, Method> of(K key, V value) {
        return new Pair(key, value);
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public String toString() {
        return "Pair [key=" + this.key + ", value=" + this.value + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof cn.hutool.core.lang.Pair)) {
            return false;
        } else {
            cn.hutool.core.lang.Pair<?, ?> pair = (cn.hutool.core.lang.Pair)o;
            return Objects.equals(this.getKey(), pair.getKey()) && Objects.equals(this.getValue(), pair.getValue());
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.key) ^ Objects.hashCode(this.value);
    }
}

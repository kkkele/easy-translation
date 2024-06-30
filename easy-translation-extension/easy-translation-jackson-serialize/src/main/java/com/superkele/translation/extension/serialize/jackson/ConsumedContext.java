package com.superkele.translation.extension.serialize.jackson;

import java.util.HashSet;
import java.util.Set;

public class ConsumedContext {

    private static final ThreadLocal<Set<Integer>> CONSUMED = ThreadLocal.withInitial(HashSet::new);


    public static boolean isConsumed(Object obj) {
        if (obj == null) {
            return false;
        }
        return CONSUMED.get().contains(obj.hashCode());
    }

    public static boolean addToConsumed(Object object) {
        if (object == null) {
            return false;
        }
        return CONSUMED.get().add(object.hashCode());
    }

    public static void clean() {
        CONSUMED.remove();
    }
}

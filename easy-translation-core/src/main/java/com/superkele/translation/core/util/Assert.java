package com.superkele.translation.core.util;

import com.superkele.translation.core.exception.AssertException;

public class Assert {

    public static void notNull(Object object) {
        notNull(object, "param should not be null");
    }

    public static void notNull(Object object, String message) {
        isTrue(object != null, message);
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new AssertException(message);
        }
    }
}

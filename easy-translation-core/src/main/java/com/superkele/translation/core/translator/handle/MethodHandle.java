package com.superkele.translation.core.translator.handle;

@FunctionalInterface
public interface MethodHandle {
    Object invoke(Object... parameters);
}

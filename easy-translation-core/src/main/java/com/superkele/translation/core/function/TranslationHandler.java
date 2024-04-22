package com.superkele.translation.core.function;


@FunctionalInterface
public interface TranslationHandler<T,R> {

    R translate(T args);

}

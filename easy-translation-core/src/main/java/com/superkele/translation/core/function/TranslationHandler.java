package com.superkele.translation.core.function;


@FunctionalInterface
public interface TranslationHandler<T,R> {

    T translate(R args);

}

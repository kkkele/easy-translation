package com.superkele.translation.core.translator;


@FunctionalInterface
public interface Translator {
    Object doTranslate(Object... args);
}

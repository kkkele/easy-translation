package com.superkele.translation.core.function;


public interface Translator {
    default Object executeTranslator(Object... args) {
        return null;
    }
}

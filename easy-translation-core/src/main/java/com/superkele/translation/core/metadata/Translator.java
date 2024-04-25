package com.superkele.translation.core.metadata;


public interface Translator {
    default Object executeTranslator(Object... args) {
        return null;
    }
}

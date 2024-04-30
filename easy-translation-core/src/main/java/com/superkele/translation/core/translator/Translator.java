package com.superkele.translation.core.translator;


public interface Translator {
    default Object doTranslate(Object... args) {
        return null;
    }
}

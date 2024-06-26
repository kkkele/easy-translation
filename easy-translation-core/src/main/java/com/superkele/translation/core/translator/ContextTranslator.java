package com.superkele.translation.core.translator;


@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();

    @Override
    default Object doTranslate(Object... args) {
        return translate();
    }
}

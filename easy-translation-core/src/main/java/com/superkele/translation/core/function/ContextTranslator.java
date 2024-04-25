package com.superkele.translation.core.function;

@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();

    @Override
    default Object executeTranslator(Object... args) {
        return translate();
    }
}

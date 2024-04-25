package com.superkele.translation.core.function;

import com.superkele.translation.core.metadata.Translator;

@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();

    @Override
    default Object executeTranslator(Object... args) {
        return translate();
    }
}

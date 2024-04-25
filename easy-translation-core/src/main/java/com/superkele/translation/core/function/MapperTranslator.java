package com.superkele.translation.core.function;


import com.superkele.translation.core.metadata.Translator;

@FunctionalInterface
public interface MapperTranslator extends Translator {

    Object translate(Object mapper);

    @Override
    default Object executeTranslator(Object... args) {
        return translate(args[0]);
    }
}

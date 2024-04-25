package com.superkele.translation.core.function;


@FunctionalInterface
public interface MapperTranslator extends Translator {

    Object translate(Object mapper);

    @Override
    default Object executeTranslator(Object... args) {
        return translate(args[0]);
    }
}

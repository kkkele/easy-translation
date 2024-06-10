package com.superkele.translation.core.translator;


@FunctionalInterface
public interface MapperTranslator extends Translator {

    Object translate(Object var0);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0]);
    }
}

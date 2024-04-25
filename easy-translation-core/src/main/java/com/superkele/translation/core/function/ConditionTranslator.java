package com.superkele.translation.core.function;

@FunctionalInterface
public interface ConditionTranslator extends Translator {

    Object translate(Object mapper, Object other);

    @Override
    default Object executeTranslator(Object... args) {
        return translate(args[0], args[1]);
    }
}

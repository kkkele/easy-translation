package com.superkele.translation.core.function;

import com.superkele.translation.core.metadata.Translator;

@FunctionalInterface
public interface ConditionTranslator extends Translator {

    Object translate(Object mapper, Object other);

    @Override
    default Object executeTranslator(Object... args) {
        return translate(args[0], args[1]);
    }
}

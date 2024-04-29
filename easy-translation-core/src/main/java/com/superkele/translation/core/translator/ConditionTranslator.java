package com.superkele.translation.core.translator;


@FunctionalInterface
public interface ConditionTranslator extends Translator {

    Object translate(Object mapper, Object other);
}

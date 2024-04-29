package com.superkele.translation.core.translator;


@FunctionalInterface
public interface ConditionTranslator extends Translator {

    Object translate(Object var0, Object var1);
}

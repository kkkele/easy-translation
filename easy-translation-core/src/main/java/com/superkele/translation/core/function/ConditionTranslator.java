package com.superkele.translation.core.function;

@FunctionalInterface
public interface ConditionTranslator extends Translator{

    Object translate(Object mapper,Object other);
}

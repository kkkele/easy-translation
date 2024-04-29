package com.superkele.translation.core.translator;


@FunctionalInterface
public interface ConditionTranslator{

    Object translate(Object mapper, Object other);
}

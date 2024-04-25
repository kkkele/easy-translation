package com.superkele.translation.core.function;

@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();
}

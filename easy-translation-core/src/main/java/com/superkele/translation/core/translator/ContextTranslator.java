package com.superkele.translation.core.translator;

@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();
}

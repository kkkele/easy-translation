package com.superkele.translation.core.translator;


@FunctionalInterface
public interface MapperTranslator extends Translator{

    Object translate(Object mapper);

}

package com.superkele.translation.core.factory;

public interface ConfigurableTranslatorFactory extends TranslationFactory {

    void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor);


}

package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;

public interface ConfigurableTranslatorFactory extends TranslatorFactory {

    String[] getTranslatorNames();

    void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor);
}

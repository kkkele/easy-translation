package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;

public interface ConfigurableTransExecutorFactory extends TransExecutorFactory {

    String[] getTranslatorNames();

    void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor);
}

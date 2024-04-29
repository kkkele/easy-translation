package com.superkele.translation.core.factory;

import com.superkele.translation.core.definition.TranslatorDefinition;

public interface ConfigurableTranslatorFactory extends TranslationFactory {

    void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor);

    TranslatorDefinition findTranslatorDefinition(String translatorName);
}

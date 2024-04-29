package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;

public interface ConfigurableTranslatorFactory extends TranslationFactory {

    TranslatorDefinition getTranslatorDefinition(String translatorName);

    void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor);
}

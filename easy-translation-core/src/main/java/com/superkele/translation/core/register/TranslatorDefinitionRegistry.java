package com.superkele.translation.core.register;

import com.superkele.translation.core.metadata.TranslatorDefinition;

public interface TranslatorDefinitionRegistry {

    void register(String translatorName, TranslatorDefinition definition);

    TranslatorDefinition getTranslatorDefinition(String translatorName);

    String[] getTranslatorNames();
}

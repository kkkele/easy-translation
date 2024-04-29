package com.superkele.translation.core.translator.definition;

public interface TranslatorDefinitionRegistry {

    void register(String translatorName, TranslatorDefinition definition);

    TranslatorDefinition getTranslatorDefinition(String translatorName);

    String[] getTranslatorNames();
}

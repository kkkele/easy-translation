package com.superkele.translation.core.translator.definition;

public interface TranslatorDefinitionFactory {

    /**
     * 根据名称获取TranslatorDefinition
     *
     * @param translatorName 翻译器名称
     * @return
     */
    TranslatorDefinition findTranslatorDefinition(String translatorName);
}

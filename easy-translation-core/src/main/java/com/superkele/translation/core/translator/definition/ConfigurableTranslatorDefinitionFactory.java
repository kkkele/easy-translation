package com.superkele.translation.core.translator.definition;

import com.superkele.translation.core.translator.factory.AutowireCapableTranslatorFactory;
import com.superkele.translation.core.translator.factory.ConfigurableTranslatorFactory;

public interface ConfigurableTranslatorDefinitionFactory extends AutowireCapableTranslatorFactory, ConfigurableTranslatorFactory {

    /**
     * 根据名称获取TranslatorDefinition
     *
     * @param translatorName 翻译器名称
     * @return
     */
    TranslatorDefinition findTranslatorDefinition(String translatorName);

}

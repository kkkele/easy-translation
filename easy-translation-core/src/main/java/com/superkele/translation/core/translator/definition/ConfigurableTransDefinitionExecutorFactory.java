package com.superkele.translation.core.translator.definition;

import com.superkele.translation.core.translator.factory.AutowireCapableTransExecutorFactory;
import com.superkele.translation.core.translator.factory.ConfigurableTransExecutorFactory;

public interface ConfigurableTransDefinitionExecutorFactory extends AutowireCapableTransExecutorFactory, ConfigurableTransExecutorFactory {

    /**
     * 根据名称获取TranslatorDefinition
     *
     * @param translatorName 翻译器名称
     * @return
     */
    TranslatorDefinition findTranslatorDefinition(String translatorName);

}

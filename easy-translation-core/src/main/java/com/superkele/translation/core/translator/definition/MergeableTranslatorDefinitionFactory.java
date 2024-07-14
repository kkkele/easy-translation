package com.superkele.translation.core.translator.definition;

import com.superkele.translation.core.translator.factory.ConfigurableTranslatorFactory;

/**
 * 可合并的工厂
 */
public interface MergeableTranslatorDefinitionFactory extends TranslatorDefinitionFactory, ConfigurableTranslatorFactory {

    void merge(MergeableTranslatorDefinitionFactory factory);
}

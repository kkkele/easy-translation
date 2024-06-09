package com.superkele.translation.core.translator.definition;

/**
 * 修改TranslatorDefinition信息
 */
public interface TranslatorFactoryPostProcessor {

    /**
     * 在所有TranslatorDefinition加载完成后，但在translator实例化之前，提供修改TranslatorDefintion属性值的机制
     */
    void postProcess(ConfigurableTranslatorFactory factory);

}

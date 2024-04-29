package com.superkele.translation.core.context.support;

import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;

/**
 * 抽象翻译器上下文
 */
public abstract class AbstractTranslatorContext implements ConfigurableTranslatorContext {
    @Override
    public void refresh() {
        //创建TranslatorFactory,并加载TranslatorDefinition
        refreshTranslatorFactory();
        //获取translatorFactor
        ConfigurableTranslatorDefinitionFactory translatorFactory = getTranslatorFactory();
        //在translator正式转载前，对translatorFactory进行一些初始化操作
        invokeTranslatorFactoryPostProcessors(translatorFactory);
    }


    protected abstract void invokeTranslatorFactoryPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory);

    protected abstract void refreshTranslatorFactory();

    @Override
    public Translator findTranslator(String translator) {
        return getTranslatorFactory().findTranslator(translator);
    }

    @Override
    public <T extends Translator> T findTranslator(String name, Class<T> requireType) {
        return getTranslatorFactory().findTranslator(name, requireType);
    }

    @Override
    public boolean containsTranslator(String name) {
        return getTranslatorFactory().containsTranslator(name);
    }

    public abstract ConfigurableTranslatorDefinitionFactory getTranslatorFactory();
}

package com.superkele.translation.core.context.support;

import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorFactory;

import java.util.Arrays;

/**
 * 抽象翻译器上下文
 */
public abstract class AbstractTranslatorContext implements ConfigurableTranslatorContext {
    @Override
    public void refresh() {
        //创建TranslatorFactory,并加载TranslatorDefinition
        refreshTranslatorFactory();
        //获取translatorFactor
        ConfigurableTranslatorFactory translatorFactory = getTranslatorFactory();
        //在translator正式转载前，对translatorFactory进行一些初始化操作
        invokeTranslatorFactoryPostProcessors(translatorFactory);
        //装载translatorPostProcessor
        loadTranslatorPostProcessors(translatorFactory);
        //实例化translator
        loadTranslators(translatorFactory);
    }

    protected void loadTranslators(ConfigurableTranslatorFactory translatorFactory) {
        Arrays.stream(translatorFactory.getTranslatorNames())
                .map(translatorFactory::findTranslator)
                .forEach(translator -> {
                });
    }

    protected abstract void loadTranslatorPostProcessors(ConfigurableTranslatorFactory translatorFactory);


    protected abstract void invokeTranslatorFactoryPostProcessors(ConfigurableTranslatorFactory translatorFactory);

    protected abstract void refreshTranslatorFactory();

    @Override
    public Translator findTranslator(String translator) {
        return getTranslatorFactory().findTranslator(translator);
    }

    @Override
    public boolean containsTranslator(String name) {
        return getTranslatorFactory().containsTranslator(name);
    }

    public abstract ConfigurableTranslatorFactory getTranslatorFactory();
}
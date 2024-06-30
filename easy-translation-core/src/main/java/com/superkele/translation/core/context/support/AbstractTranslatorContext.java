package com.superkele.translation.core.context.support;

import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.util.LogUtils;

import java.util.Arrays;

/**
 * 抽象翻译器上下文
 */
public abstract class AbstractTranslatorContext implements ConfigurableTranslatorContext {


    @Override
    public void refresh() {
        //创建TranslatorFactory,并加载TranslatorDefinition
        refreshTranslatorFactory();
        //获取translatorFactory
        ConfigurableTranslatorDefinitionFactory translatorFactory = getTranslatorFactory();
        //在translator正式转载前，对translatorFactory进行一些初始化操作
        invokeTranslatorFactoryPostProcessors(translatorFactory);
        //装载translatorPostProcessor
        loadTranslatorPostProcessors(translatorFactory);
        //实例化translator
        loadTranslators(translatorFactory);
        //回调触发其他事件
        noticeListeners();
    }

    protected abstract void noticeListeners();

    protected void loadTranslators(ConfigurableTranslatorDefinitionFactory translatorFactory) {
        Arrays.stream(translatorFactory.getTranslatorNames())
                .map(translatorName -> {
                    try {
                        return translatorFactory.findTranslator(translatorName);
                    } catch (RuntimeException e) {
                        LogUtils.error(System.err::printf, "load translator error:\n%s\n", () -> e);
                        return null;
                    }
                })
                .forEach(translator -> {
                });
    }

    protected abstract void loadTranslatorPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory);

    protected abstract void invokeTranslatorFactoryPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory);

    protected abstract void refreshTranslatorFactory();

    @Override
    public Translator findTranslator(String translator) {
        return getTranslatorFactory().findTranslator(translator);
    }

    @Override
    public boolean containsTranslator(String name) {
        return getTranslatorFactory().containsTranslator(name);
    }

    public abstract ConfigurableTranslatorDefinitionFactory getTranslatorFactory();
}
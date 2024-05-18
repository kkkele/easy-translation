package com.superkele.translation.core.context.support;

import com.superkele.translation.core.context.ConfigurableTransExecutorContext;
import com.superkele.translation.core.translator.definition.ConfigurableTransDefinitionExecutorFactory;
import com.superkele.translation.core.translator.handle.TranslateExecutor;

import java.util.Arrays;

/**
 * 抽象翻译器上下文
 */
public abstract class AbstractTransExecutorContext implements ConfigurableTransExecutorContext {
    @Override
    public void refresh() {
        //创建TranslatorFactory,并加载TranslatorDefinition
        refreshTranslatorFactory();
        //获取translatorFactor
        ConfigurableTransDefinitionExecutorFactory translatorFactory = getTranslatorFactory();
        //在translator正式转载前，对translatorFactory进行一些初始化操作
        invokeTranslatorFactoryPostProcessors(translatorFactory);
        //装载translatorPostProcessor
        loadTranslatorPostProcessors(translatorFactory);
        //实例化translator
        loadTranslators(translatorFactory);
    }

    protected void loadTranslators(ConfigurableTransDefinitionExecutorFactory translatorFactory) {
        Arrays.stream(translatorFactory.getTranslatorNames())
                .map(translatorFactory::findExecutor)
                .forEach(translator -> {
                });
    }

    protected abstract void loadTranslatorPostProcessors(ConfigurableTransDefinitionExecutorFactory translatorFactory);


    protected abstract void invokeTranslatorFactoryPostProcessors(ConfigurableTransDefinitionExecutorFactory translatorFactory);

    protected abstract void refreshTranslatorFactory();

    @Override
    public TranslateExecutor findExecutor(String translator) {
        return getTranslatorFactory().findExecutor(translator);
    }

    @Override
    public boolean containsTranslator(String name) {
        return getTranslatorFactory().containsTranslator(name);
    }

    public abstract ConfigurableTransDefinitionExecutorFactory getTranslatorFactory();
}

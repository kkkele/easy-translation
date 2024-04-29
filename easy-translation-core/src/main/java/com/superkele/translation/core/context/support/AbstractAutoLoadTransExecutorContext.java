package com.superkele.translation.core.context.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.ConfigurableTransDefinitionExecutorFactory;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTransExecutorFactory;
import com.superkele.translation.core.translator.support.LambdaTranslatorDefinitionReader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractAutoLoadTransExecutorContext extends AbstractRefreshableTransExecutorContext {

    protected List<TranslatorFactoryPostProcessor> translatorFactoryPostProcessors = new CopyOnWriteArrayList<>();

    @Override
    protected void loadTranslatorDefinition(DefaultTransExecutorFactory translatorFactory) {
        LambdaTranslatorDefinitionReader definitionReader = new LambdaTranslatorDefinitionReader(translatorFactory, getConfig());
        if (getLocations() != null) {
            /**
             * 装载静态方法
             */
            definitionReader.loadStaticTranslatorDefinitions(getLocations());
            /**
             * 装载枚举类
             */
            definitionReader.loadEnumTranslatorDefinitions(getLocations());
        }
        if (getRegisterObjs() != null) {
            /**
             * 装载动态方法
             */
            definitionReader.loadVirtualTranslatorDefinitions(getRegisterObjs());
        }
    }

    @Override
    protected void invokeTranslatorFactoryPostProcessors(ConfigurableTransDefinitionExecutorFactory translatorFactory) {
        for (TranslatorFactoryPostProcessor translatorFactoryPostProcessor : translatorFactoryPostProcessors) {
            translatorFactoryPostProcessor.postProcess(translatorFactory);
        }
    }

    public void addTranslatorFactoryPostProcessor(TranslatorFactoryPostProcessor translatorFactoryPostProcessor) {
        translatorFactoryPostProcessors.remove(translatorFactoryPostProcessor);
        translatorFactoryPostProcessors.add(translatorFactoryPostProcessor);
    }

    protected abstract String[] getLocations();

    protected abstract Object[] getRegisterObjs();

    protected abstract Config getConfig();
}

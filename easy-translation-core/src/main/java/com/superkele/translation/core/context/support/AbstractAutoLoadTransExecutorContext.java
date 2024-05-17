package com.superkele.translation.core.context.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.ConfigurableTransDefinitionExecutorFactory;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTransExecutorFactory;
import com.superkele.translation.core.translator.support.ExecutorParamInvokeFactoryPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractAutoLoadTransExecutorContext extends AbstractRefreshableTransExecutorContext {

    protected List<TranslatorFactoryPostProcessor> translatorFactoryPostProcessors = new CopyOnWriteArrayList<>();

    @Override
    protected void loadTranslatorDefinition(DefaultTransExecutorFactory translatorFactory) {
        DefaultTranslatorDefinitionReader definitionReader = new DefaultTranslatorDefinitionReader(translatorFactory, getConfig());
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
            definitionReader.loadDynamicTranslatorDefinitions(getRegisterObjs());
        }
    }

    @Override
    protected void invokeTranslatorFactoryPostProcessors(ConfigurableTransDefinitionExecutorFactory translatorFactory) {
        addFirstTranslatorFactoryPostProcessor(new ExecutorParamInvokeFactoryPostProcessor());
        for (TranslatorFactoryPostProcessor translatorFactoryPostProcessor : translatorFactoryPostProcessors) {
            translatorFactoryPostProcessor.postProcess(translatorFactory);
        }
    }

    public void addTranslatorFactoryPostProcessorBefore(TranslatorFactoryPostProcessor translatorFactoryPostProcessor,
                                                        Class<? extends TranslatorFactoryPostProcessor> clazz) {
        translatorFactoryPostProcessors.remove(translatorFactoryPostProcessor);
        for (int i = 0; i < translatorFactoryPostProcessors.size(); i++) {
            if (translatorFactoryPostProcessors.get(i).getClass().equals(clazz)) {
                translatorFactoryPostProcessors.add(i, translatorFactoryPostProcessor);
                return;
            }
        }
        translatorFactoryPostProcessors.add(0, translatorFactoryPostProcessor);
    }

    public void addTranslatorFactoryPostProcessorAfter(TranslatorFactoryPostProcessor translatorFactoryPostProcessor,
                                                       Class<? extends TranslatorFactoryPostProcessor> clazz) {
        translatorFactoryPostProcessors.remove(translatorFactoryPostProcessor);
        for (int i = 0; i < translatorFactoryPostProcessors.size(); i++) {
            if (translatorFactoryPostProcessors.get(i).getClass().equals(clazz)) {
                translatorFactoryPostProcessors.add(i + 1, translatorFactoryPostProcessor);
                return;
            }
        }
        translatorFactoryPostProcessors.add(translatorFactoryPostProcessor);
    }

    public void addFirstTranslatorFactoryPostProcessor(TranslatorFactoryPostProcessor translatorFactoryPostProcessor) {
        translatorFactoryPostProcessors.remove(translatorFactoryPostProcessor);
        translatorFactoryPostProcessors.add(0, translatorFactoryPostProcessor);
    }

    public void addTranslatorFactoryPostProcessor(TranslatorFactoryPostProcessor translatorFactoryPostProcessor) {
        translatorFactoryPostProcessors.remove(translatorFactoryPostProcessor);
        translatorFactoryPostProcessors.add(translatorFactoryPostProcessor);
    }

    protected abstract String[] getLocations();

    protected abstract Object[] getRegisterObjs();

    protected abstract Config getConfig();
}

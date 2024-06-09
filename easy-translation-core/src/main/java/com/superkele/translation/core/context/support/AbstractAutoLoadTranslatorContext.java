package com.superkele.translation.core.context.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorFactory;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTranslatorFactory;
import com.superkele.translation.core.translator.support.ExecutorParamInvokeFactoryPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractAutoLoadTranslatorContext extends AbstractRefreshableTranslatorContext {

    protected List<TranslatorFactoryPostProcessor> translatorFactoryPostProcessors = new CopyOnWriteArrayList<>();

    protected List<TranslatorPostProcessor> translatorPostProcessors = new CopyOnWriteArrayList<>();

    protected abstract DefaultTranslatorDefinitionReader getDefinitionReader();

    public void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor) {
        translatorPostProcessors.remove(translatorPostProcessor);
        translatorPostProcessors.add(translatorPostProcessor);
    }

    @Override
    protected void loadTranslatorPostProcessors(ConfigurableTranslatorFactory translatorFactory) {
        translatorPostProcessors.forEach(translatorFactory::addTranslatorPostProcessor);
    }

    @Override
    protected void loadTranslatorDefinition(DefaultTranslatorFactory translatorFactory) {
        DefaultTranslatorDefinitionReader definitionReader = getDefinitionReader();
        definitionReader.setConfig(getConfig());
        /**
         * 装载静态方法
         */
        definitionReader.loadStaticTranslatorDefinitions(translatorFactory);
        /**
         * 装载枚举类
         */
        definitionReader.loadEnumTranslatorDefinitions(translatorFactory);
        if (getRegisterObjs() != null) {
            /**
             * 装载动态方法
             */
            definitionReader.loadDynamicTranslatorDefinitions(getRegisterObjs(), translatorFactory);
        }
    }

    @Override
    protected void invokeTranslatorFactoryPostProcessors(ConfigurableTranslatorFactory translatorFactory) {
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

    protected abstract Object[] getRegisterObjs();

    protected abstract Config getConfig();
}

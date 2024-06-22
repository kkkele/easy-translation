package com.superkele.translation.core.context.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
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


    public void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor) {
        translatorPostProcessors.remove(translatorPostProcessor);
        translatorPostProcessors.add(translatorPostProcessor);
    }

    @Override
    protected void loadTranslatorPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory) {
        translatorPostProcessors.forEach(translatorFactory::addTranslatorPostProcessor);
    }

    @Override
    protected void loadTranslatorDefinition(DefaultTranslatorFactory translatorFactory) {
        DefaultTranslatorDefinitionReader definitionReader = new DefaultTranslatorDefinitionReader(translatorFactory);
        definitionReader.loadTranslatorDefinitions(getBasePackages());
    }

    @Override
    protected void invokeTranslatorFactoryPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory) {
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

    protected abstract Config getConfig();

    protected abstract String[] getBasePackages();
}

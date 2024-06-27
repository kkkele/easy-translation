package com.superkele.translation.core.context.support;


import com.superkele.translation.core.config.DefaultTranslatorNameGenerator;
import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTranslatorFactory;
import com.superkele.translation.core.translator.support.ExecutorParamInvokeFactoryPostProcessor;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class AbstractAutoLoadTranslatorContext extends AbstractRefreshableTranslatorContext {

    protected List<TranslatorFactoryPostProcessor> translatorFactoryPostProcessors = new CopyOnWriteArrayList<>();

    protected List<TranslatorPostProcessor> translatorPostProcessors = new CopyOnWriteArrayList<>();

    private List<Consumer<ConfigurableTranslatorContext>> listeners = new LinkedList<>();

    @Override
    protected void noticeListeners() {
        for (Consumer<ConfigurableTranslatorContext> listener : listeners) {
            listener.accept(this);
        }
    }

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
        DefaultTranslatorDefinitionReader definitionReader = new DefaultTranslatorDefinitionReader(translatorFactory, getTranslatorNameGenerator(), getTranslatorClazzMap());
        definitionReader.loadTranslatorDefinitions(getBasePackages());
    }

    protected abstract Map<Integer, Class<? extends Translator>> getTranslatorClazzMap();

    protected abstract DefaultTranslatorNameGenerator getTranslatorNameGenerator();

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

    protected abstract String[] getBasePackages();

    @Override
    public void register(Consumer<ConfigurableTranslatorContext> consumer) {
        this.listeners.remove(consumer);
        this.listeners.add(consumer);
    }

}

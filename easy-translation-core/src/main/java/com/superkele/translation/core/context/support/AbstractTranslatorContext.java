package com.superkele.translation.core.context.support;

import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 抽象翻译器上下文
 */
public abstract class AbstractTranslatorContext implements ConfigurableTranslatorContext {

    private List<Consumer<ConfigurableTranslatorContext>> listeners = new CopyOnWriteArrayList<>();

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
        loadTranslators(translatorFactory.getTranslatorNames(), translatorFactory);
        //通知监听器
        notice();
    }

    @Override
    public void notice() {
        listeners.forEach(listener -> listener.accept(this));
    }

    @Override
    public void addListener(Consumer<ConfigurableTranslatorContext> action) {
        listeners.add(action);
    }

    protected void loadTranslators(String[] translatorNames, ConfigurableTranslatorDefinitionFactory translatorFactory) {
        for (String translatorName : translatorNames) {
            try {
                TransManager.getTransLog().trace("load translator ---> {}", () -> translatorName);
                translatorFactory.findTranslator(translatorName);
            } catch (RuntimeException e) {
                TransManager.getTransLog().error("load translator error:\n{}\n", () -> e);
            }
        }
    }

    protected abstract void loadTranslatorPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory);

    protected abstract void invokeTranslatorFactoryPostProcessors(ConfigurableTranslatorDefinitionFactory translatorFactory);

    protected abstract void refreshTranslatorFactory();

    @Override
    public Translator findTranslator(String translator) {
        return getTranslatorFactory().findTranslator(translator);
    }

    @Override
    public TranslatorDefinition findTranslatorDefinition(String translatorName) {
        return getTranslatorFactory().findTranslatorDefinition(translatorName);
    }

    @Override
    public boolean containsTranslator(String name) {
        return getTranslatorFactory().containsTranslator(name);
    }

    public abstract ConfigurableTranslatorDefinitionFactory getTranslatorFactory();
}
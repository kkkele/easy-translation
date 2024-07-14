package com.superkele.translation.core.context.support;

import com.superkele.translation.core.invoker.support.ConfigurableInvokeBeanFactory;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.support.DefaultTranslatorFactory;

public abstract class AbstractRefreshableTranslatorContext extends AbstractDynamicTranslatorContext {

    private DefaultTranslatorFactory translatorFactory;

    public AbstractRefreshableTranslatorContext() {
        getInvokeBeanFactory().addListener(invokeBeanFactory -> refresh());
    }

    @Override
    protected void refreshTranslatorFactory() {
        DefaultTranslatorFactory translatorFactory = createTranslatorFactory();
        loadTranslatorDefinition(translatorFactory);
        this.translatorFactory = translatorFactory;
    }

    @Override
    public TranslatorDefinition findTranslatorDefinition(String translatorName) {
        return translatorFactory.findTranslatorDefinition(translatorName);
    }

    @Override
    public void mergeToMain(ConfigurableTranslatorDefinitionFactory translatorFactory) {
        getTranslatorFactory().merge(translatorFactory);
    }

    protected DefaultTranslatorFactory createTranslatorFactory() {
        return new DefaultTranslatorFactory(getInvokeBeanFactory());
    }

    @Override
    public ConfigurableTranslatorDefinitionFactory createTempTranslatorDefinitionFactory(String... path) {
        DefaultTranslatorFactory translatorFactory = createTranslatorFactory();
        loadTranslatorDefinition(translatorFactory, path);
        return translatorFactory;
    }

    protected abstract ConfigurableInvokeBeanFactory getInvokeBeanFactory();

    @Override
    public ConfigurableTranslatorDefinitionFactory getTranslatorFactory() {
        return translatorFactory;
    }

    protected abstract void loadTranslatorDefinition(DefaultTranslatorFactory translatorFactory);

    protected abstract void loadTranslatorDefinition(DefaultTranslatorFactory translatorFactory, String... path);
}

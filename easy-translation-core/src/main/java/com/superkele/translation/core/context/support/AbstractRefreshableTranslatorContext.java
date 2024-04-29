package com.superkele.translation.core.context.support;

import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.support.DefaultTranslatorFactory;

public abstract class AbstractRefreshableTranslatorContext extends AbstractTranslatorContext {

    private DefaultTranslatorFactory translatorFactory;

    @Override
    protected void refreshTranslatorFactory() {
        DefaultTranslatorFactory translatorFactory = createTranslatorFactory();
        loadTranslatorDefinition(translatorFactory);
        this.translatorFactory = translatorFactory;
    }

    protected DefaultTranslatorFactory createTranslatorFactory() {
        return new DefaultTranslatorFactory();
    }

    @Override
    public ConfigurableTranslatorDefinitionFactory getTranslatorFactory() {
        return translatorFactory;
    }

    protected abstract void loadTranslatorDefinition(DefaultTranslatorFactory translatorFactory);
}

package com.superkele.translation.core.context.support;

import com.superkele.translation.core.translator.definition.ConfigurableTransDefinitionExecutorFactory;
import com.superkele.translation.core.translator.support.DefaultTransExecutorFactory;

public abstract class AbstractRefreshableTransExecutorContext extends AbstractTransExecutorContext {

    private DefaultTransExecutorFactory translatorFactory;

    @Override
    protected void refreshTranslatorFactory() {
        DefaultTransExecutorFactory translatorFactory = createTranslatorFactory();
        loadTranslatorDefinition(translatorFactory);
        this.translatorFactory = translatorFactory;
    }

    protected DefaultTransExecutorFactory createTranslatorFactory() {
        return new DefaultTransExecutorFactory();
    }

    @Override
    public ConfigurableTransDefinitionExecutorFactory getTranslatorFactory() {
        return translatorFactory;
    }

    protected abstract void loadTranslatorDefinition(DefaultTransExecutorFactory translatorFactory);
}

package com.superkele.translation.core.context.support;

import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorFactory;
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
        return new DefaultTranslatorFactory(getInvokeBeanFactory());
    }

    protected abstract InvokeBeanFactory getInvokeBeanFactory();

    @Override
    public ConfigurableTranslatorFactory getTranslatorFactory() {
        return translatorFactory;
    }

    protected abstract void loadTranslatorDefinition(DefaultTranslatorFactory translatorFactory);
}

package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.mapping.support.DefaultTranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationFactory;
import com.superkele.translation.core.metadata.support.DefaultConfigurableFieldTranslationFactory;
import com.superkele.translation.core.translator.factory.TranslatorFactory;

import java.util.concurrent.Executor;


public class DefaultTranslationProcessor extends AsyncableTranslationProcessor {


    private final TranslationInvoker translationInvoker;
    private final Config config;


    public DefaultTranslationProcessor(TranslationInvoker translationInvoker, FieldTranslationFactory fieldTranslationFactory, Config config) {
        super(fieldTranslationFactory);
        this.translationInvoker = translationInvoker;
        this.config = config;
    }

    public DefaultTranslationProcessor(TranslatorFactory translatorFactory, FieldTranslationFactory fieldTranslationFactory, Config config) {
        super(fieldTranslationFactory);
        this.translationInvoker = new DefaultTranslationInvoker(translatorFactory);
        this.config = config;
    }

    @Override
    protected boolean getAsyncEnabled() {
        return config.getAsyncEnabled().get();
    }

    @Override
    protected TranslationInvoker getTranslationInvoker() {
        return this.translationInvoker;
    }

    @Override
    protected Executor getExecutor() {
        return config.getThreadPoolExecutor();
    }

    @Override
    protected boolean getCacheEnabled() {
        return config.getCacheEnabled().get();
    }
}

package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import com.superkele.translation.core.util.PropertyUtils;

import java.util.List;

import java.util.concurrent.*;


public class OnceFieldTranslationHandler extends AbstractFieldTranslationHandler {

    private final TranslatorFactory translatorFactory;

    private final TranslationProcessor translationProcessor;

    public OnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources, TranslatorFactory translatorFactory, TranslationProcessor translationProcessor) {
        super(fieldTranslation, sources);
        this.translatorFactory = translatorFactory;
        this.translationProcessor = translationProcessor;
    }

    @Override
    protected TranslationProcessor getTranslationProcessor() {
        return translationProcessor;
    }

    @Override
    protected Executor getExecutorService() {
        return Config.INSTANCE.getThreadPoolExecutor();
    }

    @Override
    protected boolean getAsyncEnabled() {
        return Config.INSTANCE.getThreadPoolExecutor() != null;
    }

    @Override
    protected TranslatorFactory getTranslatorFactory() {
        return translatorFactory;
    }

    @Override
    protected PropertyHandler getPropertyHandler() {
        return PropertyUtils.getPropertyHandler();
    }
}

package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import java.util.List;

import java.util.concurrent.*;


public class OnceFieldTranslationHandler extends AbstractFieldTranslationHandler {

    private final TranslatorFactory translatorFactory;

    public OnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources, TranslatorFactory translatorFactory) {
        super(fieldTranslation, sources);
        this.translatorFactory = translatorFactory;
    }

    @Override
    protected ExecutorService getExecutorService() {
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
}

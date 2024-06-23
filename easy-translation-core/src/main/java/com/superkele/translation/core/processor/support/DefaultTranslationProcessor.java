package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.support.DefaultConfigurableFieldTranslationFactory;
import com.superkele.translation.core.translator.factory.TranslatorFactory;


public class DefaultTranslationProcessor extends AsyncableTranslationProcessor {


    public DefaultTranslationProcessor(TranslatorFactory translatorFactory, DefaultConfigurableFieldTranslationFactory fieldTranslationFactory) {
        super(translatorFactory, fieldTranslationFactory);
    }

    @Override
    protected boolean getAsyncEnable() {
        return Config.INSTANCE.getThreadPoolExecutor() != null;
    }


}

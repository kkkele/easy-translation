package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.annotation.support.DefaultMappingHandler;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.factory.TranslatorFactory;


public class DefaultTranslationProcessor extends AsyncableTranslationProcessor {

    private DefaultMappingHandler mappingHandler;

    private PropertyHandler propertyHandler;


    public DefaultTranslationProcessor(TranslatorFactory translatorFactory, PropertyHandler propertyHandler) {
        this.propertyHandler = propertyHandler;
        this.mappingHandler = new DefaultMappingHandler(translatorFactory, propertyHandler);
    }



    @Override
    protected boolean getAsyncEnable() {
        return Config.INSTANCE.getThreadPoolExecutor() != null;
    }


}

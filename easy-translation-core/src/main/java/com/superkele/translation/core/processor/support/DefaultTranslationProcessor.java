package com.superkele.translation.core.processor.support;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.support.MappingFiledTranslationBuilder;
import com.superkele.translation.core.translator.factory.TranslatorFactory;


public class DefaultTranslationProcessor extends AsyncableTranslationProcessor {


    private final FieldTranslationBuilder mappingFiledTranslationBuilder;

    public DefaultTranslationProcessor(TranslatorFactory translatorFactory, FieldTranslationBuilder mappingFiledTranslationBuilder) {
        super(translatorFactory);
        this.mappingFiledTranslationBuilder = mappingFiledTranslationBuilder;
    }


    @Override
    protected FieldTranslationBuilder getMappingFieldTranslationBuilder() {
        return mappingFiledTranslationBuilder;
    }

    @Override
    protected boolean getAsyncEnable() {
        return Config.INSTANCE.getThreadPoolExecutor() != null;
    }


}

package com.superkele.translation.core.metadata.support;

import com.superkele.translation.core.context.support.AbstractTranslatorContext;
import com.superkele.translation.core.mapping.ParamHandlerResolver;
import com.superkele.translation.core.mapping.ResultHandlerResolver;
import com.superkele.translation.core.metadata.ConfigurableFieldTranslationFactory;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationRegistry;

public class DefaultConfigurableFieldTranslationFactory implements ConfigurableFieldTranslationFactory, FieldTranslationRegistry {

    private final String[] basePath;
    protected DefaultFieldTranslationFactory fieldTranslationFactory;
    private FieldTranslationBuilder builder;

    public DefaultConfigurableFieldTranslationFactory(AbstractTranslatorContext translatorContext,
                                                      ParamHandlerResolver paramHandlerResolver,
                                                      ResultHandlerResolver resultHandlerResolver, String... basePath) {
        this.basePath = basePath;
        translatorContext.register(context -> {
            builder = new DefaultMappingFiledTranslationBuilder(translatorContext.getTranslatorFactory(), paramHandlerResolver, resultHandlerResolver);
            refresh();
        });
    }

    @Override
    public void refresh() {
        fieldTranslationFactory = new DefaultFieldTranslationFactory(builder);
        DefaultFiledTranslationReader defaultFiledTranslationReader = new DefaultFiledTranslationReader(this, builder);
        defaultFiledTranslationReader.load(basePath);
    }

    @Override
    public FieldTranslation get(Class<?> clazz, boolean isJsonSerialize) {
        return fieldTranslationFactory.get(clazz, isJsonSerialize);
    }

    @Override
    public void register(Class<?> clazz, boolean isJsonSerialize, FieldTranslation fieldTranslation) {
        fieldTranslationFactory.register(clazz, isJsonSerialize, fieldTranslation);
    }
}

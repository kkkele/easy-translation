package com.superkele.translation.core.metadata.support;

import com.superkele.translation.core.metadata.ConfigurableFieldTranslationFactory;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationRegistry;

public class DefaultConfigurableFieldTranslationFactory implements ConfigurableFieldTranslationFactory, FieldTranslationRegistry {

    private final FieldTranslationBuilder builder;
    private final String[] basePath;
    protected DefaultFieldTranslationFactory fieldTranslationFactory;

    public DefaultConfigurableFieldTranslationFactory(FieldTranslationBuilder builder, String... basePath) {
        this.builder = builder;
        this.basePath = basePath;
        refresh();
    }

    @Override
    public void refresh() {
        fieldTranslationFactory = new DefaultFieldTranslationFactory(builder);
        DefaultFiledTranslationReader defaultFiledTranslationReader = new DefaultFiledTranslationReader(fieldTranslationFactory,builder);
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

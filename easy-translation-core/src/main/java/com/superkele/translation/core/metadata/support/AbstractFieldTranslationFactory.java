package com.superkele.translation.core.metadata.support;

import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationFactory;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractFieldTranslationFactory implements FieldTranslationFactory {


    protected abstract Map<Class<?>, FieldTranslation> getJsonFieldTranslationBucket();
    protected abstract Map<Class<?>, FieldTranslation> getCommonTranslationBucket();


    FieldTranslationBuilder fieldTranslationBuilder = new MappingFiledTranslationBuilder();

    @Override
    public FieldTranslation get(Class<?> clazz, boolean isJsonSerialize) {
        if (isJsonSerialize) {
            return Optional.ofNullable(getJsonFieldTranslationBucket().get(clazz))
                    .orElseGet(() -> fieldTranslationBuilder.build(clazz, true));
        }
        return Optional.ofNullable(getCommonTranslationBucket().get(clazz))
                .orElseGet(() -> fieldTranslationBuilder.build(clazz, true));
    }
}

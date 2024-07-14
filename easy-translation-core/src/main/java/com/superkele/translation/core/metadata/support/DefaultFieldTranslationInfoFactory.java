package com.superkele.translation.core.metadata.support;

import com.superkele.translation.core.metadata.FieldTranslationBuilder;

public class DefaultFieldTranslationInfoFactory extends AbstractFieldTranslationInfoFactory {

    private final FieldTranslationBuilder builder;

    public DefaultFieldTranslationInfoFactory(FieldTranslationBuilder builder) {
        this.builder = builder;
    }

    @Override
    protected FieldTranslationBuilder getBuilder() {
        return builder;
    }

}

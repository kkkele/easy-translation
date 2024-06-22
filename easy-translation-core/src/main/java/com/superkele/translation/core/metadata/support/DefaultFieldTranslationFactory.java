package com.superkele.translation.core.metadata.support;

import com.superkele.translation.core.metadata.FieldTranslationBuilder;

public class DefaultFieldTranslationFactory extends AbstractFieldTranslationFactory {

    private final FieldTranslationBuilder builder;

    public DefaultFieldTranslationFactory(FieldTranslationBuilder builder) {
        this.builder = builder;
    }

    @Override
    protected FieldTranslationBuilder getBuilder() {
        return builder;
    }

}

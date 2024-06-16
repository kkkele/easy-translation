package com.superkele.translation.extension.serialize.jackson;

import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.annotation.support.DefaultMappingHandler;
import com.superkele.translation.core.property.support.PropertyHandler;
import com.superkele.translation.core.translator.factory.TranslatorFactory;

public class TranslationSerializerModifier extends FilterSerializerModifier {

    private final TranslatorFactory translatorFactory;

    private final PropertyHandler propertyHandler;

    private  MappingHandler mappingHandler;

    public TranslationSerializerModifier(TranslatorFactory translatorFactory, PropertyHandler propertyHandler) {
        this.translatorFactory = translatorFactory;
        this.propertyHandler = propertyHandler;
        this.mappingHandler = new DefaultMappingHandler(translatorFactory,propertyHandler);
    }


    @Override
    protected MappingHandler getMappingHandler() {
        return mappingHandler;
    }
}

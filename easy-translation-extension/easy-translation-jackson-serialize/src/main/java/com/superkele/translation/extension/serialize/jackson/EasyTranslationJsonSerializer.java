package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.serializer.JsonNodeProcessor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class EasyTranslationJsonSerializer extends JsonSerializer{


    private static final ThreadLocal<JsonNodeProcessor> JSON_NODE_PROCESSOR = new ThreadLocal<>();

    private final FieldTranslation fieldTranslation;

    public EasyTranslationJsonSerializer(FieldTranslation fieldTranslation) {
        this.fieldTranslation = fieldTranslation;
    }

    @Override
    public void serialize(Object propertyValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object obj = jsonGenerator.currentValue();
        System.out.println(propertyValue);
        System.out.println(obj);
        System.out.println(fieldTranslation);
    }
}

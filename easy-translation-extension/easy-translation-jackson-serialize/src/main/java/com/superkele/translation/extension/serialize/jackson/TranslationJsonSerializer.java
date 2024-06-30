package com.superkele.translation.extension.serialize.jackson;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.collection.ListUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.support.AbstractOnceFieldTranslationHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;

@Slf4j
public class TranslationJsonSerializer extends JsonSerializer {


    private static final Set<Integer> BEAN_CONSUMED = new ConcurrentHashSet<>();
    private final FieldTranslation fieldTranslation;
    private final TranslationInvoker translationInvoker;
    private final JsonSerializer serializer;

    public TranslationJsonSerializer(FieldTranslation fieldTranslation, TranslationInvoker translationInvoker, JsonSerializer serializer) {
        this.fieldTranslation = fieldTranslation;
        this.translationInvoker = translationInvoker;
        this.serializer = serializer;
    }

    @Override
    public void serialize(Object propertyValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (BEAN_CONSUMED.contains(propertyValue.hashCode())) {
            serializer.serialize(propertyValue, jsonGenerator, serializerProvider);
            return;
        }
        Object currentValue = jsonGenerator.getCurrentValue();
        if (currentValue == null) {
            JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslation, Collections.singletonList(propertyValue));
            jsonOnceFieldTranslationHandler.handle();
        } else {
            if (currentValue instanceof List) {
                if (!BEAN_CONSUMED.contains(currentValue.hashCode())) {
                    List<Object> list = (List<Object>) currentValue;
                    JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslation, list);
                    jsonOnceFieldTranslationHandler.handle();
                    BEAN_CONSUMED.add(currentValue.hashCode());
                }
            } else if (currentValue.getClass().isArray()) {
                if (!BEAN_CONSUMED.contains(currentValue.hashCode())) {
                    ArrayList<Object> list = ListUtil.toList((Object[]) currentValue);
                    JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslation, list);
                    jsonOnceFieldTranslationHandler.handle();
                    BEAN_CONSUMED.add(currentValue.hashCode());
                }
            } else if (currentValue instanceof Set) {
                if (!BEAN_CONSUMED.contains(currentValue.hashCode())) {
                    JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslation, new ArrayList<>((Set) currentValue));
                    jsonOnceFieldTranslationHandler.handle();
                    BEAN_CONSUMED.add(currentValue.hashCode());
                }
            } else {
                JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslation, Collections.singletonList(propertyValue));
                jsonOnceFieldTranslationHandler.handle();
            }
        }
        BEAN_CONSUMED.add(propertyValue.hashCode());
        try {
            jsonGenerator.writeObject(propertyValue);
        } finally {
            BEAN_CONSUMED.remove(propertyValue.hashCode());
        }
    }

    public class JsonOnceFieldTranslationHandler extends AbstractOnceFieldTranslationHandler {

        public JsonOnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources) {
            super(fieldTranslation, sources);
        }

        @Override
        protected boolean getCacheEnabled() {
            return true;
        }

        @Override
        protected TranslationInvoker getTranslationInvoker() {
            return translationInvoker;
        }

        @Override
        protected Executor getExecutor() {
            return null;
        }

        @Override
        protected boolean getAsyncEnabled() {
            return false;
        }

        @Override
        protected void cleanAsyncEnv() {

        }

        @Override
        protected void buildAsyncEnv() {

        }

        @Override
        protected void processHook(int sourceIndex, FieldTranslationEvent event) {

        }
    }
}

package com.superkele.translation.extension.serialize.jackson;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.support.AbstractOnceFieldTranslationHandler;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Slf4j
public class TranslationJsonSerializer extends JsonSerializer {

    private final FieldTranslation fieldTranslation;
    private final TranslationInvoker translationInvoker;
    private final JsonSerializer serializer;
    private final Config config;

    public TranslationJsonSerializer(FieldTranslation fieldTranslation, TranslationInvoker translationInvoker, JsonSerializer serializer, Config config) {
        this.fieldTranslation = fieldTranslation;
        this.translationInvoker = translationInvoker;
        this.serializer = serializer;
        this.config = config;
        LogUtils.debug(log::debug, "{} => TranslationJsonSerializer init...", () -> fieldTranslation.getName());
    }

    @Override
    public void serialize(Object propertyValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object currentValue = jsonGenerator.getCurrentValue();
        if (currentValue == null) {
            translate(Collections.singletonList(propertyValue));
            serializer.serialize(propertyValue, jsonGenerator, serializerProvider);
            return;
        }
        handleCurrentValue(currentValue, propertyValue);
        serializer.serialize(propertyValue, jsonGenerator, serializerProvider);
    }

    private void handleCurrentValue(Object currentValue, Object propertyValue) throws IOException {
        if (currentValue instanceof List) {
            handleCollection((Collection<?>) currentValue);
        } else if (currentValue.getClass().isArray()) {
            handleCollection(Arrays.asList((Object[]) currentValue));
        } else if (currentValue instanceof Set) {
            handleCollection(new ArrayList<>((Set<?>) currentValue));
        } else {
            translate(Collections.singletonList(propertyValue));
        }
    }

    private void handleCollection(Collection<?> collection) throws IOException {
        if (!ConsumedContext.isConsumed(collection)) {
            translate(new CopyOnWriteArrayList<>(collection));
            ConsumedContext.addToConsumed(collection);
        }
    }

    private void translate(List<Object> list) {
        JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslation, list);
        jsonOnceFieldTranslationHandler.handle();
    }


    public class JsonOnceFieldTranslationHandler extends AbstractOnceFieldTranslationHandler {

        public JsonOnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources) {
            super(fieldTranslation, sources);
        }

        @Override
        protected boolean getCacheEnabled() {
            return config.getCacheEnabled().get();
        }

        @Override
        protected TranslationInvoker getTranslationInvoker() {
            return translationInvoker;
        }

        @Override
        protected Executor getExecutor() {
            return config.getThreadPoolExecutor();
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

package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.config.TranslationConfig;
import com.superkele.translation.core.context.DynamicTranslatorContext;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.mapping.support.DefaultTranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationInfo;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.support.AbstractOnceFieldTranslationHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

@Slf4j
public class TranslationJsonSerializer extends JsonSerializer {

    private final FieldTranslationInfo fieldTranslationInfo;
    private final JsonSerializer serializer;
    private final TranslationInvoker invoker = new DefaultTranslationInvoker();

    public TranslationJsonSerializer(FieldTranslationInfo fieldTranslationInfo, JsonSerializer serializer) {
        this.fieldTranslationInfo = fieldTranslationInfo;
        this.serializer = serializer;
        TransLog transLog = TransManager.getTransLog();
        transLog.debug("{} => TranslationJsonSerializer init...", () -> fieldTranslationInfo.getName());
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
        JsonOnceFieldTranslationHandler jsonOnceFieldTranslationHandler = new JsonOnceFieldTranslationHandler(fieldTranslationInfo, list);
        jsonOnceFieldTranslationHandler.handle();
    }


    public class JsonOnceFieldTranslationHandler extends AbstractOnceFieldTranslationHandler {

        public JsonOnceFieldTranslationHandler(FieldTranslationInfo fieldTranslationInfo, List<Object> sources) {
            super(fieldTranslationInfo, sources);
        }

        @Override
        protected boolean getCacheEnabled() {
            return TransManager.getConfig().isCacheEnabled();
        }

        @Override
        protected TranslationInvoker getTranslationInvoker() {
            return invoker;
        }

        @Override
        protected Executor getExecutor() {
            return TransManager.getConfig().getThreadPoolExecutor();
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

package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.support.MappingFiledTranslationBuilder;
import com.superkele.translation.core.thread.ContextPasser;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends AbstractTranslationProcessor {

    /**
     * key：主类
     * value：处理后的，需要翻译的字段
     */
    private final Map<Class<?>, FieldTranslation> fieldTranslationMap = new ConcurrentHashMap<>();
    private final MappingFiledTranslationBuilder mappingFiledTranslationBuilder = new MappingFiledTranslationBuilder();

    @Override
    protected <T> void processInternal(Map<Class<?>, List<?>> classMap, boolean async) {
        if (async) {
            List<ContextPasser> contextPassers = buildContextPasser();
            contextPassers.forEach(contextPasser -> contextPasser.setPassValue());
            CompletableFuture[] array = classMap.keySet()
                    .stream()
                    .map(clazz -> {
                        FieldTranslation fieldTranslation = fieldTranslationMap.get(clazz);
                        List<?> list = classMap.get(clazz);
                        return CompletableFuture.runAsync(() -> {
                            contextPassers.forEach(contextPasser -> contextPasser.passContext());
                            OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(this, fieldTranslation);
                            onceFieldTranslationHandler.handle(list, getAsyncEnable());
                            contextPassers.forEach(contextPasser -> contextPasser.clearContext());
                        });
                    })
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(array).join();
        } else {
            classMap.forEach((clazz, list) -> {
                FieldTranslation fieldTranslation = fieldTranslationMap.get(clazz);
                OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(this, fieldTranslation);
                onceFieldTranslationHandler.handle(list, getAsyncEnable());
            });
        }
    }

    protected abstract boolean getAsyncEnable();

    @Override
    protected void processInternal(Object obj, Class<?> clazz) {
        FieldTranslation fieldTranslation = fieldTranslationMap.get(clazz);
        OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(this, fieldTranslation);
        onceFieldTranslationHandler.handle(obj);
    }

    @Override
    protected Boolean predictFilter(Class<?> clazz) {
        FieldTranslation fieldTranslation = mappingFiledTranslationBuilder.build(clazz, false);
        Optional.ofNullable(fieldTranslation)
                .ifPresent(res -> fieldTranslationMap.put(clazz, res));
        return fieldTranslationMap.containsKey(clazz);
    }

    protected List<ContextPasser> buildContextPasser() {
        return Config.INSTANCE
                .getContextHolders()
                .stream()
                .map(ContextPasser::new)
                .collect(Collectors.toList());
    }

}


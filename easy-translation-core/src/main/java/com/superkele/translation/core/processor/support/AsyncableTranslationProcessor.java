package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.support.DefaultConfigurableFieldTranslationFactory;
import com.superkele.translation.core.thread.ContextPasser;
import com.superkele.translation.core.translator.factory.TranslatorFactory;

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
    private final TranslatorFactory translatorFactory;
    private final DefaultConfigurableFieldTranslationFactory fieldTranslationFactory;

    protected AsyncableTranslationProcessor(TranslatorFactory translatorFactory, DefaultConfigurableFieldTranslationFactory fieldTranslationFactory) {
        this.translatorFactory = translatorFactory;
        this.fieldTranslationFactory = fieldTranslationFactory;
    }


    @Override
    protected void processInternal(Map<Class, List> classMap, boolean async) {
        if (async && getAsyncEnable()) {
            List<ContextPasser> contextPassers = buildContextPasser();
            contextPassers.forEach(contextPasser -> contextPasser.setPassValue());
            CompletableFuture[] array = classMap.keySet()
                    .stream()
                    .map(clazz -> {
                        FieldTranslation fieldTranslation = fieldTranslationFactory.get(clazz, false);
                        List list = classMap.get(clazz);
                        return CompletableFuture.runAsync(() -> {
                            contextPassers.forEach(contextPasser -> contextPasser.passContext());
                            OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation, list, translatorFactory,this);
                            onceFieldTranslationHandler.handle(true);
                            contextPassers.forEach(contextPasser -> contextPasser.clearContext());
                        });
                    })
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(array).join();
        } else {
            classMap.forEach((clazz, list) -> {
                FieldTranslation fieldTranslation = fieldTranslationFactory.get(clazz, false);
                OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation, list, translatorFactory,this);
                onceFieldTranslationHandler.handle(false);
            });
        }
    }

    protected abstract boolean getAsyncEnable();

    @Override
    protected void processInternal(Object obj, Class<?> clazz) {
        FieldTranslation fieldTranslation = fieldTranslationFactory.get(clazz, false);
        OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation, ListUtil.of(obj), translatorFactory,this);
        onceFieldTranslationHandler.handle(false);
    }

    @Override
    protected Boolean predictFilter(Class<?> clazz) {
        return fieldTranslationFactory.get(clazz, false) != null;
    }

    protected List<ContextPasser> buildContextPasser() {
        return Config.INSTANCE
                .getContextHolders()
                .stream()
                .map(ContextPasser::new)
                .collect(Collectors.toList());
    }

}


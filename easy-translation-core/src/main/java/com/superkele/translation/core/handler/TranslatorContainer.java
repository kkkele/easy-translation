package com.superkele.translation.core.handler;

import cn.hutool.core.collection.CollectionUtil;
import com.superkele.translation.annotation.Translator;
import com.superkele.translation.core.exception.NotDefineException;
import com.superkele.translation.core.exception.RepeatDefineException;
import com.superkele.translation.core.function.TranslationHandler;
import com.superkele.translation.core.scaner.PackageScanner;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.MethodConvert;
import com.superkele.translation.core.util.Pair;

import java.lang.invoke.LambdaConversionException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class TranslatorContainer {
    private final PackageScanner scanner;
    private Map<Translator, TranslationHandler> translatorMap;
    private Map<String, TranslationHandler> defaultHandlerMap;
    private Map<String, Map<String, TranslationHandler>> conditionHandlerMap;

    public TranslatorContainer(PackageScanner scanner) {
        this.scanner = scanner;
        this.defaultHandlerMap = buildDefaultHandlerMap();
        this.conditionHandlerMap = buildConditionHandlerMap();
    }

    public Map<String, Map<String, TranslationHandler>> getConditionHandlerMap() {
        return conditionHandlerMap;
    }

    public Map<String, TranslationHandler> getDefaultHandlerMap() {
        return defaultHandlerMap;
    }

    private TranslationHandler convertToTranslationHandler(Object convertObject, Method method) {
        try {
            return MethodConvert.convertToFunctionInterface(TranslationHandler.class, convertObject, method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Translator, TranslationHandler> getTranslatorMap() {
        if (translatorMap != null) {
            return translatorMap;
        } else {
            synchronized (this) {
                if (translatorMap != null) {
                    return translatorMap;
                }
                List<Pair<Object, Method>> methodList = scanner.scanTranslator();
                Map<Translator, TranslationHandler> collect = methodList
                        .stream()
                        .collect(Collectors.toMap(pair -> {
                            Method value = pair.getValue();
                            return value.getAnnotation(Translator.class);
                        }, pair -> convertToTranslationHandler(pair.getKey(), pair.getValue())));
                translatorMap = collect;
                return translatorMap;
            }
        }
    }

    private Map<String, TranslationHandler> buildDefaultHandlerMap() {
        Map<Translator, TranslationHandler> translatorMap = getTranslatorMap();
        Map<String, TranslationHandler> defaultHandlerMap = new HashMap<>();
        translatorMap.forEach((key, value) -> {
            if (key.isDefault()) {
                defaultHandlerMap.put(key.value(), value);
            }
        });
        return defaultHandlerMap;
    }

    private Map<String, Map<String, TranslationHandler>> buildConditionHandlerMap() {
        Map<Translator, TranslationHandler> translatorMap = getTranslatorMap();
        Map<String, Map<String, TranslationHandler>> conditionHandlerMap = new HashMap<>();
        translatorMap.forEach((translator, handler) -> {
            Map<String, TranslationHandler> childMap = conditionHandlerMap.computeIfAbsent(translator.value(), key -> new HashMap<>());
            if (childMap.containsKey(translator.other())) {
                throw new RepeatDefineException();
            }
            childMap.put(translator.other(), handler);
        });
        return conditionHandlerMap;
    }


    public TranslationHandler findTranslationHandler(String translator, String other) {
        Map<String, TranslationHandler> otherMap = conditionHandlerMap.get(translator);
        Assert.notNull(otherMap, String.format("Translator (%s) is never defined", translator));
        TranslationHandler translationHandler = otherMap.get(other);
        return Optional.ofNullable(translationHandler)
                .orElse(defaultHandlerMap.get(translator));
    }
}


























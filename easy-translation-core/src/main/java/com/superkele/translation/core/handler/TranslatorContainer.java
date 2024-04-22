package com.superkele.translation.core.handler;

import com.superkele.translation.core.function.TranslationHandler;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.MethodConvert;

import java.lang.invoke.LambdaConversionException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class TranslatorContainer implements ITranslatorContainer {
    protected Map<String, TranslationHandler> defaultHandlerMap;
    protected Map<String, Map<String, TranslationHandler>> conditionHandlerMap;


    protected TranslationHandler convertToTranslationHandler(Object convertObject, Method method) {
        try {
            return MethodConvert.convertToFunctionInterface(TranslationHandler.class, convertObject, method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        }
    }

/*    private Map<String, TranslationHandler> buildDefaultHandlerMap() {
        Map<Translator, TranslationHandler> translatorMap = getTranslatorMap();
        Map<String, TranslationHandler> defaultHandlerMap = new HashMap<>();
        translatorMap.forEach((key, value) -> {
            if (key.isDefault()) {
                defaultHandlerMap.put(key.name(), value);
            }
        });
        return defaultHandlerMap;
    }

    private Map<String, Map<String, TranslationHandler>> buildConditionHandlerMap() {
        Map<Translator, TranslationHandler> translatorMap = getTranslatorMap();
        Map<String, Map<String, TranslationHandler>> conditionHandlerMap = new HashMap<>();
        translatorMap.forEach((translator, handler) -> {
            Map<String, TranslationHandler> childMap = conditionHandlerMap.computeIfAbsent(translator.name(), key -> new HashMap<>());
            if (childMap.containsKey(translator.other())) {
                throw new RepeatDefineException();
            }
            childMap.put(translator.other(), handler);
        });
        return conditionHandlerMap;
    }*/


    @Override
    public TranslationHandler findTranslationHandler(String translator, String other) {
        Map<String, TranslationHandler> otherMap = conditionHandlerMap.get(translator);
        Assert.notNull(otherMap, String.format("Translator (%s) is never defined", translator));
        TranslationHandler translationHandler = otherMap.get(other);
        return Optional.ofNullable(translationHandler)
                .orElse(defaultHandlerMap.get(translator));
    }

}


























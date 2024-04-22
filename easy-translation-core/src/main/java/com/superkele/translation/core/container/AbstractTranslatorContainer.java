package com.superkele.translation.core.container;

import com.superkele.translation.core.function.TranslationHandler;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.MethodConvert;

import java.lang.invoke.LambdaConversionException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AbstractTranslatorContainer implements ITranslatorContainer {
    protected final Map<String, TranslationHandler> defaultHandlerMap;
    protected final Map<String, Map<String, TranslationHandler>> conditionHandlerMap;

    public AbstractTranslatorContainer(Map<String, TranslationHandler> defaultHandlerMap, Map<String, Map<String, TranslationHandler>> conditionHandlerMap) {
        this.defaultHandlerMap = defaultHandlerMap;
        this.conditionHandlerMap = conditionHandlerMap;
    }

    protected TranslationHandler convertToTranslationHandler(Object convertObject, Method method) {
        try {
            return MethodConvert.convertToFunctionInterface(TranslationHandler.class, convertObject, method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TranslationHandler findTranslationHandler(String translator, String other) {
        Map<String, TranslationHandler> otherMap = conditionHandlerMap.get(translator);
        Assert.notNull(otherMap, String.format("Translator (%s) is never defined", translator));
        TranslationHandler translationHandler = otherMap.get(other);
        return Optional.ofNullable(translationHandler)
                .orElse(defaultHandlerMap.get(translator));
    }


    public static class TranslatorDescriber{
        private final String translatorName;
        private final String other;
        private final TranslationHandler translationHandler;

        public TranslatorDescriber(String translatorName, String other, TranslationHandler translationHandler) {
            this.translatorName = translatorName;
            this.other = other;
            this.translationHandler = translationHandler;
        }
    }
}


























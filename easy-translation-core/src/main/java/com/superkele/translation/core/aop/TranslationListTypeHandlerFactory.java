package com.superkele.translation.core.aop;

import com.superkele.translation.annotation.TranslationListTypeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationListTypeHandlerFactory {

    private Map<Class<? extends TranslationListTypeHandler>, TranslationListTypeHandler> translationListTypeHandlerMap
            = new ConcurrentHashMap<>();


    public TranslationListTypeHandler getTranslationListTypeHandler(Class<? extends TranslationListTypeHandler> clazz) {
        return translationListTypeHandlerMap.computeIfAbsent(clazz, k -> {
            try {
                return clazz.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

package com.superkele.translation.core.aop;

import com.superkele.translation.annotation.TranslationUnpackingHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationListTypeHandlerFactory {

    private Map<Class<? extends TranslationUnpackingHandler>, TranslationUnpackingHandler> translationListTypeHandlerMap = new ConcurrentHashMap<>();


    public TranslationUnpackingHandler getTranslationListTypeHandler(Class<? extends TranslationUnpackingHandler> clazz) {
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

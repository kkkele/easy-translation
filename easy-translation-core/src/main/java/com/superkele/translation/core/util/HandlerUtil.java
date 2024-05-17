package com.superkele.translation.core.util;

import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.annotation.TranslationUnpackingHandler;
import com.superkele.translation.core.aop.TranslationListTypeHandlerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerUtil {

    private static final TranslationListTypeHandlerFactory factory = new TranslationListTypeHandlerFactory();

    private static final Map<Class<? extends NullPointerExceptionHandler>, NullPointerExceptionHandler> NULL_POINTER_EXCEPTION_HANDLER_MAP = new ConcurrentHashMap<>();

    public static TranslationUnpackingHandler getInstance(Class<? extends TranslationUnpackingHandler> clazz){
        return factory.getTranslationListTypeHandler(clazz);
    }

    public static NullPointerExceptionHandler getNullPointerExceptionHandler(Class<? extends NullPointerExceptionHandler> clazz){
        return NULL_POINTER_EXCEPTION_HANDLER_MAP.computeIfAbsent(clazz, k -> {
            try {
                return clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

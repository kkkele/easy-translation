package com.superkele.translation.core.util;

import com.superkele.translation.annotation.TranslationListTypeHandler;
import com.superkele.translation.core.aop.TranslationListTypeHandlerFactory;

public class TranslationListTypeHandlerUtil {

    private static final TranslationListTypeHandlerFactory factory = new TranslationListTypeHandlerFactory();

    public static TranslationListTypeHandler getInstance(Class<? extends TranslationListTypeHandler> clazz){
        return factory.getTranslationListTypeHandler(clazz);
    }

}

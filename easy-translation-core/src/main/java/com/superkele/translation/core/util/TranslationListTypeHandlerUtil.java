package com.superkele.translation.core.util;

import com.superkele.translation.annotation.TranslationUnpackingHandler;
import com.superkele.translation.core.aop.TranslationListTypeHandlerFactory;

public class TranslationListTypeHandlerUtil {

    private static final TranslationListTypeHandlerFactory factory = new TranslationListTypeHandlerFactory();

    public static TranslationUnpackingHandler getInstance(Class<? extends TranslationUnpackingHandler> clazz){
        return factory.getTranslationListTypeHandler(clazz);
    }

}

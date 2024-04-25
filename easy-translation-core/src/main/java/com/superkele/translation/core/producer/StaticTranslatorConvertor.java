package com.superkele.translation.core.producer;

import com.superkele.translation.core.function.Translator;

import java.lang.invoke.LambdaConversionException;

public interface StaticTranslatorConvertor extends TranslatorConverter {

    Translator convert(Class<?> clazz, String methodName, Class<?>... parameterTypes);
}

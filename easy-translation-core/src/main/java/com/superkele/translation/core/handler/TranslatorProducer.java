package com.superkele.translation.core.handler;

import com.superkele.translation.core.function.Translator;

import java.lang.invoke.LambdaConversionException;
import java.lang.reflect.Method;

public interface TranslatorProducer {

    Translator produce(Method method) throws LambdaConversionException, IllegalAccessException;
}

package com.superkele.translation.core.producer;

import com.superkele.translation.core.metadata.Translator;

public interface StaticTranslatorConvertor extends TranslatorConverter {

    Translator convert(Class<?> clazz, String methodName, Class<?>... parameterTypes);
}

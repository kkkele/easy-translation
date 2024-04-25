package com.superkele.translation.core.producer;

import com.superkele.translation.core.metadata.Translator;

public interface VirtualTranslatorConvertor extends TranslatorConverter {

    Translator convert(Object invokeObj, Class<?> clazz, String methodName, Class<?>... parameterTypes);
}

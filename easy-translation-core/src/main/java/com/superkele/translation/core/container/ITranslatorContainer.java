package com.superkele.translation.core.container;

import com.superkele.translation.core.function.TranslationHandler;

import java.lang.reflect.Method;

public interface ITranslatorContainer {

    void register(Method method, String translatorName, String other, boolean isDefault);

    void register(Method method, String translatorName, boolean isDefault);

    void register(Method method, String translatorName, String other);

    void register(Method method, String translatorName);

    TranslationHandler findTranslationHandler(String translator, String other);
}

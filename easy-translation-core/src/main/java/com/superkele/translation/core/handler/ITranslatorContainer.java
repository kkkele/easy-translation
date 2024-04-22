package com.superkele.translation.core.handler;

import com.superkele.translation.core.function.TranslationHandler;

public interface ITranslatorContainer {


    TranslationHandler findTranslationHandler(String translator, String other);
}

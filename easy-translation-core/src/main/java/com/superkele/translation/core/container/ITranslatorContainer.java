package com.superkele.translation.core.container;

import com.superkele.translation.core.function.TranslationHandler;

public interface ITranslatorContainer {


    TranslationHandler findTranslationHandler(String translator, String other);
}

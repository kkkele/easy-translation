package com.superkele.translation.core.container;

import com.superkele.translation.core.function.Translator;

public interface ITranslatorContainer {

    void register(Translator Translator, String translatorName, String other, boolean isDefault);

    Translator findTranslationHandler(String translator, String other);

}

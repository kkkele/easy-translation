package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.Translator;

public interface SingletonTranslatorRegistry {

    Translator getSingleton(String translatorName);

    void addSingleton(String translatorName,Translator translator);
}

package com.superkele.translation.core.translator.support;

import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.factory.SingletonTranslatorRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonTranslatorRegistry implements SingletonTranslatorRegistry {


    private final Map<String, Translator> singletonTranslatorCache = new ConcurrentHashMap<>();

    @Override
    public Translator getSingleton(String translatorName) {
        return null;
    }

    @Override
    public void addSingleton(String translatorName, Translator translator) {

    }
}

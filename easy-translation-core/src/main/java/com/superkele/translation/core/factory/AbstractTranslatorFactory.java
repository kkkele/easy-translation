package com.superkele.translation.core.factory;

import com.superkele.translation.core.definition.TranslatorDefinition;
import com.superkele.translation.core.metadata.Translator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTranslatorFactory implements ConfigurableTranslatorFactory {

    private final Map<String, Translator> translatorCache = new ConcurrentHashMap<>();

    @Override
    public Translator findTranslator(String translatorName) {
        Translator translator = translatorCache.get(translatorName);
        if (translator != null) {
            return translator;
        }
        TranslatorDefinition definition = getTranslatorDefinition(translatorName);
        translator = createTranslator(translatorName, definition);
        translatorCache.put(translatorName, translator);
        return translator;
    }


    @Override
    public <T extends Translator> T findTranslator(String name, Class<T> requireType) {
        return (T) findTranslator(name);
    }

    @Override
    public boolean containsTranslator(String name) {
        return containsTranslatorDefinition(name);
    }

    protected abstract boolean containsTranslatorDefinition(String name);

    protected abstract Translator createTranslator(String translatorName, TranslatorDefinition definition);

    protected abstract TranslatorDefinition getTranslatorDefinition(String translatorName);
}

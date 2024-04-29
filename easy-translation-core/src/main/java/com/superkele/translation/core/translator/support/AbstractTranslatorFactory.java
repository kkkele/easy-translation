package com.superkele.translation.core.translator.support;

import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.factory.ConfigurableTranslatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractTranslatorFactory implements ConfigurableTranslatorFactory {

    private final Map<String, Translator> translatorCache = new ConcurrentHashMap<>();

    private final List<TranslatorPostProcessor> translatorPostProcessors = new ArrayList<>();


    @Override
    public Translator findTranslator(String translatorName) {
        Translator translator = translatorCache.get(translatorName);
        if (translator != null) {
            return translator;
        }
        TranslatorDefinition definition = findTranslatorDefinition(translatorName);
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

    @Override
    public void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor) {
        translatorPostProcessors.remove(translatorPostProcessor);
        translatorPostProcessors.add(translatorPostProcessor);
    }

    public List<TranslatorPostProcessor> getTranslatorPostProcessors() {
        return this.translatorPostProcessors;
    }

    protected abstract boolean containsTranslatorDefinition(String name);

    protected abstract Translator createTranslator(String translatorName, TranslatorDefinition definition);

    protected abstract TranslatorDefinition findTranslatorDefinition(String translatorName);
}

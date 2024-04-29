package com.superkele.translation.core.translator.support;

import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTranslatorFactory extends AbstractAutowireCapableTranslatorFactory
        implements ConfigurableTranslatorDefinitionFactory, TranslatorDefinitionRegistry {

    private final Map<String, TranslatorDefinition> translatorDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public void register(String translatorName, TranslatorDefinition definition) {
        translatorDefinitionMap.put(translatorName, definition);
    }

    @Override
    public TranslatorDefinition getTranslatorDefinition(String translatorName) {
        return this.findTranslatorDefinition(translatorName);
    }

    @Override
    public String[] getTranslatorNames() {
        return translatorDefinitionMap.keySet().toArray(String[]::new);
    }

    @Override
    public boolean containsTranslatorDefinition(String name) {
        return translatorDefinitionMap.containsKey(name);
    }


    @Override
    public TranslatorDefinition findTranslatorDefinition(String translatorName) {
        return Optional.ofNullable(translatorDefinitionMap.get(translatorName))
                .orElseThrow(() -> new RuntimeException("No translator name '" + translatorName + "' is founded"));
    }
}

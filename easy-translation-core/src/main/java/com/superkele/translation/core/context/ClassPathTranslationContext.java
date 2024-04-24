package com.superkele.translation.core.context;

import com.superkele.translation.core.function.Translator;
import com.superkele.translation.core.loader.TranslatorLoader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ClassPathTranslationContext implements TranslationContext {

    private final Map<String, Translator> translatorMap = new ConcurrentHashMap<>();

    private final String[] basePackages;

    public ClassPathTranslationContext(String... basePackage) {
        this.basePackages = basePackage;
    }

    public void addLoader(TranslatorLoader translatorLoader) {
        for (String basePackage : basePackages) {
            translatorLoader.load(basePackage).forEach(pair -> this.register(pair.getKey(), pair.getValue()));
        }
    }

    @Override
    public void register(String name, Translator translator) {
        translatorMap.put(name, translator);
    }

    @Override
    public Translator findTranslator(String translator) {
        return Optional.ofNullable(translatorMap.get(translator))
                .orElseThrow(() -> new RuntimeException("找不到对应的翻译器"));
    }

}

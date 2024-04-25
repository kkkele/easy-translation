package com.superkele.translation.core.context;

import com.superkele.translation.core.function.Translator;

public interface TranslationFactory {

    Translator findTranslator(String translator);

    <T> T findTranslator(String name, Class<T> requireType);

    boolean containsTranslator(String name);
}

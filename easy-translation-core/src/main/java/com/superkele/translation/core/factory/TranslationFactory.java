package com.superkele.translation.core.factory;

import com.superkele.translation.core.metadata.Translator;

public interface TranslationFactory {

    Translator findTranslator(String translator);

    <T extends Translator> T findTranslator(String name, Class<T> requireType);

    boolean containsTranslator(String name);
}

package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.Translator;

public interface TranslatorFactory {

    Translator findTranslator(String translator);

    <T extends Translator> T findTranslator(String name, Class<T> requireType);

    boolean containsTranslator(String name);
}

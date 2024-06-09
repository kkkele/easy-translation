package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.Translator;

public interface TranslatorFactory {

    Translator findTranslator(String translator);

    boolean containsTranslator(String name);
}

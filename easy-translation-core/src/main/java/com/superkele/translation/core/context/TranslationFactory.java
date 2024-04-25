package com.superkele.translation.core.context;

import com.superkele.translation.core.function.Translator;

public interface TranslationFactory {

    Translator findTranslator(String translator);


}

package com.superkele.translation.core.context;

import com.superkele.translation.core.function.Translator;

public interface TranslationContext {

    void register(String name, Translator translator);

    Translator findTranslator(String translator);


}

package com.superkele.translation.core.register;

import com.superkele.translation.core.function.Translator;

public interface TranslatorRegister {

    void register(String translatorName, Translator translator);
}

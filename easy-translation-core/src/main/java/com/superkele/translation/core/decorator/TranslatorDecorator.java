package com.superkele.translation.core.decorator;

import com.superkele.translation.core.translator.ContextTranslator;
import com.superkele.translation.core.translator.Translator;

public interface TranslatorDecorator {

    Translator decorate(Translator translator);

}
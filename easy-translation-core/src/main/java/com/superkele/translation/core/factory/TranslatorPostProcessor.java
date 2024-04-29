package com.superkele.translation.core.factory;

import com.superkele.translation.core.translator.Translator;

public interface TranslatorPostProcessor {

    Translator postProcessorBeforeInit(Translator translator, String translatorName);

    Translator postProcessorAfterInit(Translator translator, String translatorName);

}

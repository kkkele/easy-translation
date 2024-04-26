package com.superkele.translation.core.factory;

import com.superkele.translation.core.metadata.Translator;

public interface TranslatorPostProcessor {

    Translator postProcessorBeforeInit(Translator translator, String translatorName);

    Translator postProcessorAfterInit(Translator translator, String translatorName);

}

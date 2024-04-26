package com.superkele.translation.core.factory;

import com.superkele.translation.core.metadata.Translator;

public interface AutowireCapableTranslatorFactory extends TranslationFactory {


    Translator applyTranslatorPostProcessorBeforeInit(Translator translator, String translatorName);

    Translator applyTranslatorPostProcessorAfterInit(Translator translator, String translatorName);


}

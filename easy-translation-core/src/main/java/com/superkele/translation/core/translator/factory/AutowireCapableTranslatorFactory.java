package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.handle.TranslateExecutor;

public interface AutowireCapableTranslatorFactory extends TranslatorFactory {


    Translator applyTranslatorPostProcessorBeforeInit(Translator translator, String translatorName);

    Translator applyTranslatorPostProcessorAfterInit(Translator translator, String translatorName);


}

package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.handle.TranslateExecutor;

public interface AutowireCapableTransExecutorFactory extends TransExecutorFactory {


    TranslateExecutor applyTranslatorPostProcessorBeforeInit(TranslateExecutor translator, String translatorName);

    TranslateExecutor applyTranslatorPostProcessorAfterInit(TranslateExecutor translator, String translatorName);


}

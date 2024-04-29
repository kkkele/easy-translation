package com.superkele.translation.core.translator.definition;


import com.superkele.translation.core.translator.handle.TranslateExecutor;

public interface TranslatorPostProcessor {

    TranslateExecutor postProcessorBeforeInit(TranslateExecutor translator, String translatorName);

    TranslateExecutor postProcessorAfterInit(TranslateExecutor translator, String translatorName);

}

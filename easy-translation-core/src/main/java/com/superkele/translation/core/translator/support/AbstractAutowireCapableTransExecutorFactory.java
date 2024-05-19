package com.superkele.translation.core.translator.support;


import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.factory.AutowireCapableTransExecutorFactory;
import com.superkele.translation.core.translator.handle.TranslateExecutor;


public abstract class AbstractAutowireCapableTransExecutorFactory extends AbstractTransExecutorFactory
        implements AutowireCapableTransExecutorFactory {


    @Override
    public TranslateExecutor createTranslator(String translatorName, TranslatorDefinition definition) {
        TranslateExecutor translator = definition.getTranslateExecutor();
        TranslateExecutor process = applyTranslatorPostProcessorBeforeInit(translator, translatorName);
        if (process != null) {
            applyTranslatorPostProcessorAfterInit(translator, translatorName);
        }
        return process;
    }

    @Override
    public TranslateExecutor applyTranslatorPostProcessorBeforeInit(TranslateExecutor translator, String translatorName) {
        TranslateExecutor result = translator;
        for (TranslatorPostProcessor translatorPostProcessor : getTranslatorPostProcessors()) {
            result = translatorPostProcessor.postProcessorBeforeInit(result, translatorName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    @Override
    public TranslateExecutor applyTranslatorPostProcessorAfterInit(TranslateExecutor translator, String translatorName) {
        TranslateExecutor result = translator;
        for (TranslatorPostProcessor translatorPostProcessor : getTranslatorPostProcessors()) {
            result = translatorPostProcessor.postProcessorAfterInit(result, translatorName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

}

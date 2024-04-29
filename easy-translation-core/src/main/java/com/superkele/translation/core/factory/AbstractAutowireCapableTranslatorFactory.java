package com.superkele.translation.core.factory;


import com.superkele.translation.core.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.Translator;


public abstract class AbstractAutowireCapableTranslatorFactory extends AbstractTranslatorFactory
        implements AutowireCapableTranslatorFactory {


    @Override
    protected Translator createTranslator(String translatorName, TranslatorDefinition definition) {
        Translator translator = definition.getTranslator();
        Translator process = applyTranslatorPostProcessorBeforeInit(translator, translatorName);
        if (process != null) {
            applyTranslatorPostProcessorAfterInit(translator, translatorName);
        }
        return process;
    }

    @Override
    public Translator applyTranslatorPostProcessorBeforeInit(Translator translator, String translatorName) {
        Translator result = translator;
        for (TranslatorPostProcessor translatorPostProcessor : getTranslatorPostProcessors()) {
            result = translatorPostProcessor.postProcessorBeforeInit(translator, translatorName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    @Override
    public Translator applyTranslatorPostProcessorAfterInit(Translator translator, String translatorName) {
        Translator result = translator;
        for (TranslatorPostProcessor translatorPostProcessor : getTranslatorPostProcessors()) {
            result = translatorPostProcessor.postProcessorAfterInit(translator, translatorName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

}

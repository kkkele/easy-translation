package com.superkele.translation.core.factory;


import com.superkele.translation.core.definition.TranslatorDefinition;
import com.superkele.translation.core.metadata.Translator;

import java.util.ArrayList;
import java.util.List;

public abstract  class AbstractAutowireCapableTranslatorFactory extends AbstractTranslatorFactory
        implements AutowireCapableTranslatorFactory{

    private final List<TranslatorPostProcessor> beanPostProcessors = new ArrayList<>();

    @Override
    protected Translator createTranslator(String translatorName, TranslatorDefinition definition) {
        return null;
    }

    @Override
    public Translator applyTranslatorPostProcessorBeforeInit(Translator translator, String translatorName) {
        return null;
    }

    @Override
    public Translator applyTranslatorPostProcessorAfterInit(Translator translator, String translatorName) {
        return null;
    }

}

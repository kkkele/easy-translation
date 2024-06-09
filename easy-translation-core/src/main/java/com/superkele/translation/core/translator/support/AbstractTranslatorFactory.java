package com.superkele.translation.core.translator.support;

import com.superkele.translation.core.invoker.enums.InvokeBeanScope;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.factory.ConfigurableTranslatorFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTranslatorFactory extends DefaultSingletonTranslatorRegistry implements ConfigurableTranslatorFactory {

    private final List<TranslatorPostProcessor> translatorPostProcessors = new ArrayList<>();


    @Override
    public Translator findTranslator(String translatorName) {
        //增加 原型Translator和单例Translator的区分
        Translator translator = getSingleton(translatorName);
        if (translator != null) {
            return translator;
        }
        TranslatorDefinition definition = findTranslatorDefinition(translatorName);
        translator = createTranslator(translatorName, definition);
        if (definition.getScope() == InvokeBeanScope.SINGLETON) {
            addSingleton(translatorName, translator);
        }
        return translator;
    }


    @Override
    public boolean containsTranslator(String name) {
        return containsTranslatorDefinition(name);
    }

    @Override
    public void addTranslatorPostProcessor(TranslatorPostProcessor translatorPostProcessor) {
        translatorPostProcessors.remove(translatorPostProcessor);
        translatorPostProcessors.add(translatorPostProcessor);
    }

    public List<TranslatorPostProcessor> getTranslatorPostProcessors() {
        return this.translatorPostProcessors;
    }

    protected abstract boolean containsTranslatorDefinition(String name);

    protected abstract Translator createTranslator(String translatorName, TranslatorDefinition definition);

    protected abstract TranslatorDefinition findTranslatorDefinition(String translatorName);
}

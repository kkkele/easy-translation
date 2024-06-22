package com.superkele.translation.core.mapping;

import com.superkele.translation.core.mapping.support.TranslationEnvironment;
import com.superkele.translation.core.translator.Translator;

import java.util.List;

public interface TranslationInvoker {

    void invoke(Object source, Translator translator, TranslationEnvironment translationEnvironment);

    void invokeBatch(List<Object> sources, Translator translator, TranslationEnvironment translationEnvironment);
}

package com.superkele.translation.core.mapping;

import com.superkele.translation.core.mapping.support.TranslationEnvironment;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.translator.Translator;

import java.util.List;
import java.util.Map;

public interface TranslationInvoker {

    void invoke(Object source, Translator translator, FieldTranslationEvent event, Map<String,Object> cache);

    void invokeBatch(List<Object> sources, Translator translator, FieldTranslationEvent event, Map<String,Object> cache);
}

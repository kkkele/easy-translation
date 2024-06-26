package com.superkele.translation.core.mapping;

import com.superkele.translation.core.metadata.FieldTranslationEvent;

import java.util.List;
import java.util.Map;

public interface TranslationInvoker {

    void invoke(Object source, FieldTranslationEvent event, Map<String,Object> cache);

    void invokeBatch(List<Object> sources,FieldTranslationEvent event, Map<String,Object> cache);
}

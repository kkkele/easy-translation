package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.processor.TranslationProcessor;

public abstract class AbstractTranslationProcessor extends FilterTranslationProcessor {

    @Override
    public void process(Object obj) {
        process(obj, obj.getClass());
    }
}

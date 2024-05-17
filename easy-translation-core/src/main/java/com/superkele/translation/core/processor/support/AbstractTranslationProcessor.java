package com.superkele.translation.core.processor.support;


public abstract class AbstractTranslationProcessor extends FilterTranslationProcessor {

    @Override
    public void process(Object obj) {
        process(obj, obj.getClass());
    }
}

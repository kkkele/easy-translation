package com.superkele.translation.core.handler.support;

import com.superkele.translation.core.handler.TranslationProcessor;

import java.util.Collection;

public abstract class ListableTranslatorProcessorWrapper implements TranslationProcessor {
    private final TranslationProcessor processor;

    public ListableTranslatorProcessorWrapper(TranslationProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void process(Object obj) {
        if (obj instanceof Collection<?> coll) {
            for (Object o : coll) {
                processor.process(o);
            }
        }
    }

}

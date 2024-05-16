package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.processor.TranslationProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class FilterTranslationProcessor implements TranslationProcessor {

    private Map<Class<?>, Boolean> filterCache = new ConcurrentHashMap<>();


    @Override
    public void process(Object obj, Class<?> clazz) {
        if (obj == null) {
            return;
        }
        if (!filter(clazz)) {
            return;
        }
        processInternal(obj, clazz);
    }

    protected boolean filter(Class<?> clazz) {
        return filterCache.computeIfAbsent(clazz, this::predictFilter);
    }

    protected abstract void processInternal(Object obj, Class<?> clazz);

    protected abstract Boolean predictFilter(Class<?> clazz);

}

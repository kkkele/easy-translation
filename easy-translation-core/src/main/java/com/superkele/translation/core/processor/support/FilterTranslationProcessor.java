package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.processor.TranslationProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;


public abstract class FilterTranslationProcessor implements TranslationProcessor {

    private Map<Class<?>, Boolean> filterCache = new ConcurrentHashMap<>();


    @Override
    public void process(Object obj, Class<?> clazz) {
        if (!filter(obj, clazz)) {
            return;
        }
        processInternal(obj, clazz, null);
    }

    protected boolean filter(Object obj, Class<?> clazz) {
        if (obj == null) {
            return false;
        }
        return filterCache.computeIfAbsent(clazz, this::predictFilter);
    }

    public void process(Object obj, Class<?> clazz, Supplier<Void> callback) {
        if (!filter(obj, clazz)) {
            return;
        }
        processInternal(obj, clazz, callback);
    }

    protected abstract void processInternal(Object obj, Class<?> clazz, Supplier<Void> callback);

    protected abstract Boolean predictFilter(Class<?> clazz);

}

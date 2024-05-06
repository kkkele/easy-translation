package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.context.TransExecutorContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class FilterTranslationProcessor extends AbstractTranslationProcessor {

    private Map<Class<?>, Boolean> filterCache = new ConcurrentHashMap<>();


    @Override
    public void process(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        Boolean exists = filterCache.computeIfAbsent(clazz, this::predictFilter);
        if (!exists) {
            return;
        }
        processInternal(obj, clazz);
    }

    protected abstract void processInternal(Object obj, Class<?> clazz);

    protected abstract Boolean predictFilter(Class<?> clazz);

}

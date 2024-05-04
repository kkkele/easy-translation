package com.superkele.translation.core.handler.support;

import com.superkele.translation.core.context.TransExecutorContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public abstract class FilterTranslationProcessor extends AbstractTranslationProcessor {

    private Map<Class<?>, Boolean> filterCache = new ConcurrentHashMap<>();

    protected abstract TransExecutorContext getContext();

    @Override
    public void process(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        Boolean filter = filterCache.computeIfAbsent(clazz, this::predictFilter);
        if (filter) {
            return;
        }
        processInternal(obj, clazz);
    }

    protected abstract void processInternal(Object obj, Class<?> clazz);

    protected abstract Boolean predictFilter(Class<?> clazz);

}

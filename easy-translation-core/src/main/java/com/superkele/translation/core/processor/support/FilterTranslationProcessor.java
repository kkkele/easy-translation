package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.CollectionUtil;
import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.processor.TranslationProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class FilterTranslationProcessor implements TranslationProcessor {

    private Map<Class<?>, Boolean> filterCache = new ConcurrentHashMap<>();

    @Override
    public void process(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        if (!filter(clazz)) {
            return;
        }
        processInternal(obj, clazz);
    }

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

    @Override
    public void processBatch(Collection<BeanDescription> collection, boolean async) {
        if (CollectionUtil.isEmpty(collection)) {
            return;
        }
        Map<Class<?>, List<?>> classMap = new HashMap<>();
        collection.forEach(beanDescription -> {
            if (!filter(beanDescription.getClazz())) {
                return;
            }
            List list = classMap.computeIfAbsent(beanDescription.getClazz(), key -> new ArrayList<>());
            list.add(beanDescription.getBean());
        });
        processInternal(classMap, async);
    }

    protected abstract <T> void processInternal(Map<Class<?>, List<?>> classMap, boolean async);

    protected boolean filter(Class<?> clazz) {
        return filterCache.computeIfAbsent(clazz, this::predictFilter);
    }

    protected abstract void processInternal(Object obj, Class<?> clazz);

    protected abstract Boolean predictFilter(Class<?> clazz);

}

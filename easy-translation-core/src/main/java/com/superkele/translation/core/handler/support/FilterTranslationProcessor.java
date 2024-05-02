package com.superkele.translation.core.handler.support;

import cn.hutool.core.bean.BeanUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.handler.TranslationProcessor;
import com.superkele.translation.core.metadata.FieldInfo;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
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

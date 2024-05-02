package com.superkele.translation.core.handler.support;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends FilterTranslationProcessor {

    protected List<ContextHolder> contextHolders = new ArrayList<>();

    private Map<Class<?>, FieldTranslation> fieldTranslationMap;

    protected abstract ThreadPoolExecutor getThreadPoolExecutor();

    protected abstract boolean getAsyncEnable();

    public void addContextHolders(ContextHolder contextHolder) {
        contextHolders.remove(contextHolder);
        contextHolders.add(contextHolder);
    }

    @Override
    protected Boolean predictFilter(Class<?> clazz) {
        Field[] fields = ReflectUtils.getFields(clazz);
        List<Pair<Field, Mapping>> mappingFields = new ArrayList<>();
        for (Field field : fields) {
            Optional.ofNullable(AnnotatedElementUtils.getMergedAnnotation(field, Mapping.class))
                    .map(mapping -> Pair.of(field, mapping))
                    .ifPresent(mappingFields::add);
        }
        return fieldTranslationMap.containsKey(clazz);
    }
}

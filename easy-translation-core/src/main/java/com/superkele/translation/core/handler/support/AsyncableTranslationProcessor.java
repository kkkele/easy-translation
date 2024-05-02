package com.superkele.translation.core.handler.support;

import cn.hutool.core.collection.CollectionUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends FilterTranslationProcessor {

    protected List<ContextHolder> contextHolders = new ArrayList<>();

    private Map<Class<?>, FieldTranslation> fieldTranslationMap;

    protected abstract ThreadPoolExecutor getThreadPoolExecutor();

    protected abstract boolean getAsyncEnable();

    protected abstract Translator getTranslator(String translatorName);

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
                    .filter(mapping -> mapping.timing() == TranslateTiming.AFTER_RETURN)
                    .map(mapping -> Pair.of(field, mapping))
                    .ifPresent(mappingFields::add);
        }
        if (CollectionUtil.isNotEmpty(mappingFields)) {
            Map<String, Short> indexTaskMap = new HashMap<>();
            List<Short> eventMasks = new ArrayList<>();
            Map<Short, List<FieldTranslationEvent>> afterEventMap = new HashMap<>();
            List<FieldTranslationEvent> sortEvents = new ArrayList<>();
            //先将所有的mapping和field改造成FieldTranslationEvent对象
            //1.简单排序
            mappingFields.sort(Comparator.comparingInt(o -> o.getValue().sort()));
            short event = 1;
            mappingFields.forEach(pair -> {
                FieldTranslationEvent fieldTranslationEvent = new FieldTranslationEvent();
                fieldTranslationEvent.setFieldName(pair.getKey().getName());
                fieldTranslationEvent.setEvent(event);
                fieldTranslationEvent.setMapper(pair.getValue().mapper());
                fieldTranslationEvent.setOther(pair.getValue().other());
                fieldTranslationEvent.setNotNullMapping(pair.getValue().notNullMapping());
                Translator translator = getTranslator(pair.getValue().translator());
                if (getAsyncEnable() && pair.getValue().async()) {
                    fieldTranslationEvent.setAction(translateConsumer(translator));
                } else {
                    fieldTranslationEvent.setAction(translateConsumer(translator));
                }
            });
        }
        return fieldTranslationMap.containsKey(clazz);
    }

    protected Consumer<FieldTranslationEvent> translateConsumer(Translator translator) {
        return event -> {
            translateInternal(event);
        };
    }

    protected abstract void translateInternal(FieldTranslationEvent event);

    protected Consumer<FieldTranslationEvent> translateAsyncConsumer(Translator translator) {
        return event -> {
            getThreadPoolExecutor().submit(() -> {
                translateInternal(event);
            });
        };
    }
}

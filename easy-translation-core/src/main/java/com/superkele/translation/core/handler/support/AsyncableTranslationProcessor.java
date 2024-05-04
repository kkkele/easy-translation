package com.superkele.translation.core.handler.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends FilterTranslationProcessor {

    private final Map<Class<?>, FieldTranslation> fieldTranslationMap = new ConcurrentHashMap<>();

    protected List<ContextHolder> contextHolders = new ArrayList<>();

    protected abstract ThreadPoolExecutor getThreadPoolExecutor();

    protected abstract boolean getAsyncEnable();

    protected abstract TranslateExecutor getTranslateExecutor(String translatorName);

    public void addContextHolders(ContextHolder contextHolder) {
        contextHolders.remove(contextHolder);
        contextHolders.add(contextHolder);
    }

    @Override
    protected void processInternal(Object obj, Class<?> clazz) {
        FieldTranslation fieldTranslation = fieldTranslationMap.get(clazz);
        short[] afterEventMasks = fieldTranslation.getAfterEventMasks();
        Set<Short> activedAfterEventSet = new HashSet<>();
        FieldTranslationEvent[] sortEvents = fieldTranslation.getSortEvents();
        AtomicInteger activeEvent = new AtomicInteger(0);
        for (FieldTranslationEvent sortEvent : sortEvents) {
            sortEvent.translate(obj, event -> {
                activeEvent.updateAndGet(v -> v | event);
                for (short afterEventMask : afterEventMasks) {
                    if (activedAfterEventSet.contains(afterEventMask)) {
                        continue;
                    }
                    if ((activeEvent.get() & afterEventMask) == afterEventMask) {
                        FieldTranslationEvent[] afterEvents = fieldTranslation.getAfterEventMaskMap()
                                .get(afterEventMask);
                        activedAfterEventSet.add(afterEventMask);
                        for (FieldTranslationEvent afterEvent : afterEvents) {
                        }
                    }
                }
            });
        }
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
            FieldTranslation fieldTranslation = computeFieldTranslationToCache(mappingFields);
            if (ObjectUtil.isNotNull(fieldTranslation)) {
                fieldTranslationMap.put(clazz, fieldTranslation);
            }
        }
        return fieldTranslationMap.containsKey(clazz);
    }

    protected FieldTranslation computeFieldTranslationToCache(List<Pair<Field, Mapping>> mappingFields) {
        Map<String, Short> indexTaskMap = new HashMap<>();
        List<Short> eventMasks = new ArrayList<>();
        Map<Short, List<FieldTranslationEvent>> afterEventMap = new HashMap<>();
        List<FieldTranslationEvent> sortEvents = new ArrayList<>();
        //先将所有的mapping和field改造成FieldTranslationEvent对象
        //1.简单排序
        mappingFields.sort(Comparator.comparingInt(o -> o.getValue().sort()));
        short initEvent = 1;
        byte leftShift = 0;
        for (Pair<Field, Mapping> pair : mappingFields) {
            FieldTranslationEvent fieldTranslationEvent = new FieldTranslationEvent();
            fieldTranslationEvent.setFieldName(pair.getKey().getName());
            final short event = (short) (initEvent << leftShift++);
            fieldTranslationEvent.setEvent(event);
            fieldTranslationEvent.setMapper(pair.getValue().mapper());
            fieldTranslationEvent.setOther(pair.getValue().other());
            fieldTranslationEvent.setNotNullMapping(pair.getValue().notNullMapping());
            fieldTranslationEvent.setTranslateExecutor(getTranslateExecutor(pair.getValue().translator()));
        }
        return null;
    }



}

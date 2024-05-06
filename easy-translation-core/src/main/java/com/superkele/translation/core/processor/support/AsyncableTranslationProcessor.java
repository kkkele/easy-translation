package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.core.annotation.FieldTranslationInvoker;
import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.thread.ContextPasser;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends FilterTranslationProcessor {


    private final Map<Class<?>, FieldTranslation> fieldTranslationMap = new ConcurrentHashMap<>();

    protected List<ContextHolder> contextHolders = new ArrayList<>();

    protected abstract ExecutorService getThreadPoolExecutor();

    protected abstract boolean getAsyncEnable();

    protected abstract TranslateExecutor getTranslateExecutor(String translatorName);

    protected abstract MappingHandler getMappingHandler();

    protected abstract long getTimeout();


    public void addContextHolders(ContextHolder contextHolder) {
        contextHolders.remove(contextHolder);
        contextHolders.add(contextHolder);
    }

    @Override
    protected void processInternal(Object obj, Class<?> clazz) {
        FieldTranslation fieldTranslation = fieldTranslationMap.get(clazz);
        OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation);
        onceFieldTranslationHandler.handle(obj);
    }


    @Override
    protected Boolean predictFilter(Class<?> clazz) {
        Field[] fields = ReflectUtils.getFields(clazz);
        List<Pair<Field, Mapping>> mappingFields = new ArrayList<>();
        for (Field field : fields) {
            Mapping mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, Mapping.class);
            Optional.ofNullable(mergedAnnotation)
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
        //fieldName event map
        Map<String, FieldTranslationEvent> fieldTranslationMap = new HashMap<>();
        Map<Short, List<FieldTranslationEvent>> afterEventMapDTO = new HashMap<>();
        List<FieldTranslationEvent> sortEvents = new ArrayList<>();
        //先将所有的mapping和field改造成FieldTranslationEvent对象
        //1.简单排序
        mappingFields.sort(Comparator.comparingInt(o -> o.getValue().sort()));
        short initEvent = 1;
        short leftShift = 0;
        //第一次遍历，转化为FieldTranslationEvent对象
        //并放入map收集基本情况
        //查看是否需要开启缓存功能
        boolean cacheEnabled = false;
        Set<String> uniqueNameSet = new HashSet<>();
        for (Pair<Field, Mapping> pair : mappingFields) {
            Mapping mapping = pair.getValue();
            FieldTranslationEvent fieldTranslationEvent = new FieldTranslationEvent();
            final short event = (short) (initEvent << leftShift);
            fieldTranslationEvent.setFieldName(pair.getKey().getName());
            fieldTranslationEvent.setEventValue(event);
            fieldTranslationEvent.setAsync(mapping.async());
            fieldTranslationEvent.setFieldTranslationInvoker(getMappingHandler().convert(pair.getKey(), mapping));
            fieldTranslationMap.put(pair.getKey().getName(), fieldTranslationEvent);
            leftShift++;
            String uniqueName = StrUtil.join(",", mapping.translator(), mapping.mapper(), mapping.other());
            if (cacheEnabled || uniqueNameSet.contains(uniqueName)) {
                cacheEnabled = true;
            } else {
                uniqueNameSet.add(uniqueName);
            }
        }
        //第二次遍历，划分sort事件和after事件(触发的时机不同，sort事件是按顺序直接触发（一定会），after事件是回调触发（存在不执行的可能）)
        for (Pair<Field, Mapping> pair : mappingFields) {
            String fieldName = pair.getKey().getName();
            Mapping mapping = pair.getValue();
            FieldTranslationEvent fieldTranslationEvent = fieldTranslationMap.get(fieldName);
            if (mapping.after().length == 0) {
                sortEvents.add(fieldTranslationEvent);
            } else {
                short eventMask = 0;
                short[] preEvents = new short[mapping.after().length];
                int count = 0;
                for (int i = 0; i < mapping.after().length; i++) {
                    String afterFieldName = mapping.after()[i];
                    //获取前置事件
                    FieldTranslationEvent preEvent = fieldTranslationMap.get(afterFieldName);
                    if (preEvent.isAsync()) {
                        preEvents[count++] = preEvent.getEventValue();
                    }
                    Assert.isTrue(ObjectUtil.isNotNull(preEvent), "after字段必须为加了@Mapping注解(或其对应的组合注解)的字段");
                    eventMask |= preEvent.getEventValue();
                }
                fieldTranslationEvent.setPreEvents(Arrays.copyOf(preEvents, count));
                List<FieldTranslationEvent> afterEvents = afterEventMapDTO.computeIfAbsent(eventMask, key -> new ArrayList<>());
                afterEvents.add(fieldTranslationEvent);
            }
        }
        Map<Short, FieldTranslationEvent[]> afterEventsMap = new HashMap<>();
        afterEventMapDTO.forEach((eventMask, fieldTranslationEvents) -> afterEventsMap.put(eventMask, fieldTranslationEvents.toArray(FieldTranslationEvent[]::new)));
        FieldTranslation res = new FieldTranslation();
        res.setSortEvents(sortEvents.toArray(FieldTranslationEvent[]::new));
        res.setAfterEventMaskMap(afterEventsMap);
        res.setConsumeSize(mappingFields.size());
        res.setHasSameInvoker(cacheEnabled);
        return res;
    }

    /**
     * 只能用来处理一次的FieldTranslationHandler
     */
    public class OnceFieldTranslationHandler {
        private final AtomicInteger activeEvent = new AtomicInteger(0);
        private final List<ContextPasser> passerCollect = contextHolders.stream()
                .map(ContextPasser::new)
                .collect(Collectors.toList());
        /**
         * 已执行的事件集合
         */
        private final boolean cacheEnabled;
        private final CountDownLatch latch;
        private final Map<Short, AtomicInteger> countMap = new ConcurrentHashMap<>();
        protected Set<Short> consumed = new ConcurrentHashSet<>();
        private Map<String, Object> translationResCache;
        private FieldTranslation fieldTranslation;
        private ReentrantLock lock = new ReentrantLock();

        public OnceFieldTranslationHandler(FieldTranslation fieldTranslation) {
            this.fieldTranslation = fieldTranslation;
            this.latch = new CountDownLatch(fieldTranslation.getConsumeSize());
            this.cacheEnabled = fieldTranslation.isHasSameInvoker();
            if (this.cacheEnabled) {
                translationResCache = new ConcurrentHashMap<>();
            }
        }

        public void handle(Object obj) {
            if (getAsyncEnable()) {
                passerCollect.forEach(ContextPasser::setPassValue);
            }
            FieldTranslationEvent[] sortEvents = this.fieldTranslation.getSortEvents();
            //顺序执行事件
            for (FieldTranslationEvent sortEvent : sortEvents) {
                translate(obj, sortEvent);
            }
            try {
                latch.await(getTimeout(), TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //如果是after事件，需要阻塞直到前事件执行完毕
        private void translate(Object obj, FieldTranslationEvent event) {
            //如果没开启异步支持
            if (!getAsyncEnable()) {
                translateInternal(obj, event);
                return;
            }
            if (event.isAsync()) {
                //使用上下文同步器传递上下文
                CompletableFuture.runAsync(() -> {
                    passerCollect.forEach(ContextPasser::passContext);
                    translateInternal(obj, event);
                }, getThreadPoolExecutor());
            } else {
                translateInternal(obj, event);
            }
        }

        private void translateInternal(Object obj, FieldTranslationEvent event) {
            //获取事件值
            short eventValue = event.getEventValue();
            //获取单个字段翻译器
            FieldTranslationInvoker fieldTranslationInvoker = event.getFieldTranslationInvoker();
            if (cacheEnabled) {
                fieldTranslationInvoker.invoke(obj, translationResCache::get, translationResCache::put);
            } else {
                fieldTranslationInvoker.invoke(obj);
            }
            latch.countDown();
            //更新事件
            this.activeEvent.updateAndGet(current -> current | eventValue);
            //获取事件掩码集合，对比触发after事件
            fieldTranslation.getAfterEventMaskMap().forEach((eventMask, fieldTranslationEvents) -> {
                if (consumed.contains(eventMask)) {
                    return;
                }
                if ((this.activeEvent.shortValue() & eventMask) == eventMask) {
                    lock.lock();
                    try {
                        if (consumed.contains(eventMask)) {
                            return;
                        }
                        consumed.add(eventMask);
                    } finally {
                        lock.unlock();
                    }
                    //手动依次触发after事件
                    for (FieldTranslationEvent fieldTranslationEvent : fieldTranslationEvents) {
                        translate(obj, fieldTranslationEvent);
                    }
                }
            });

        }
    }
}


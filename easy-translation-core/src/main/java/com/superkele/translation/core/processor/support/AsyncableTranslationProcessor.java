package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ArrayUtil;
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
public abstract class AsyncableTranslationProcessor extends AbstractTranslationProcessor {


    private final Map<Class<?>, FieldTranslation> fieldTranslationMap = new ConcurrentHashMap<>();

    protected List<ContextHolder> contextHolders = new ArrayList<>();

    protected abstract ExecutorService getThreadPoolExecutor();

    protected abstract boolean getAsyncEnable();

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
        Map<String, FieldTranslationEvent> fieldNameEventMap = new HashMap<>();
        //记录了不同的 eventMask 可以触发的事件
        Map<Short, List<FieldTranslationEvent>> eventMaskAfterMap = new HashMap<>();
        //记录不同event值对应的不同的event
        Map<Short, FieldTranslationEvent> eventMaskMap = new HashMap<>();
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
            FieldTranslationEvent event = new FieldTranslationEvent();
            short eventValue = (short) (initEvent << leftShift);
            event.setEventValue(eventValue);
            event.setAsync(mapping.async());
            event.setFieldTranslationInvoker(getMappingHandler().convert(pair.getKey(), mapping));
            fieldNameEventMap.put(pair.getKey().getName(), event);
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
            FieldTranslationEvent event = fieldNameEventMap.get(fieldName);
            if (mapping.after().length == 0) {
                event.setTriggerMask((short) 0);
                sortEvents.add(event);
            } else {
                short eventMask = 0;
                short[] preEvents = new short[mapping.after().length];
                int count = 0;
                for (int i = 0; i < mapping.after().length; i++) {
                    String afterFieldName = mapping.after()[i];
                    //获取前置事件
                    FieldTranslationEvent preEvent = fieldNameEventMap.get(afterFieldName);
                    Assert.notNull(preEvent, "找不到名为 [" + afterFieldName + "]的前置事件，after字段必须为加了@Mapping注解(或其对应的组合注解)的字段,如果有多个参数，请使用数组传参");
                    if (preEvent.isAsync()) {
                        preEvents[count++] = preEvent.getEventValue();
                    }
                    eventMask |= preEvent.getEventValue();
                }
                List<FieldTranslationEvent> afterEvents = eventMaskAfterMap.computeIfAbsent(eventMask, key -> new ArrayList<>());
                //设置前置事件掩码
                event.setTriggerMask(eventMask);
                afterEvents.add(event);
            }
        }
        //第三次遍历，给每个事件增加 after 事件
        for (Pair<Field, Mapping> pair : mappingFields) {
            FieldTranslationEvent event = fieldNameEventMap.get(pair.getKey().getName());
            List<FieldTranslationEvent> after = new ArrayList<>();
            eventMaskAfterMap.forEach((eventMask, events) -> {
                if ((eventMask.shortValue() & event.getEventValue()) == event.getEventValue()) {
                    after.addAll(events);
                }
            });
            event.setAfterEvents(after.stream().toArray(FieldTranslationEvent[]::new));
        }
        FieldTranslation res = new FieldTranslation();
        res.setSortEvents(ArrayUtil.toArray(sortEvents, FieldTranslationEvent.class));
        res.setConsumeSize(mappingFields.size());
        res.setHasSameInvoker(cacheEnabled);
        return res;
    }

    protected List<ContextPasser> buildContextPassers() {
        return contextHolders.stream()
                .map(ContextPasser::new)
                .collect(Collectors.toList());
    }

    /**
     * 只能用来处理一次的FieldTranslationHandler
     */
    public class OnceFieldTranslationHandler {
        private final AtomicInteger activeEvent = new AtomicInteger(0);
        private final List<ContextPasser> passerCollect = buildContextPassers();
        /**
         * 已执行的事件集合
         */
        private final boolean cacheEnabled;
        private final CountDownLatch latch;
        private Set<Short> consumed = new ConcurrentHashSet<>();
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
                latch.await(getTimeout(), TimeUnit.MILLISECONDS);
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
                    passerCollect.forEach(ContextPasser::clearContext);
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
            int activeEvent = this.activeEvent.updateAndGet(current -> current | eventValue);
            //获取事件掩码集合，对比触发after事件
            for (FieldTranslationEvent afterEvent : event.getAfterEvents()) {
                if (consumed.contains(afterEvent.getEventValue())) {
                    continue;
                }
                short triggerMask = afterEvent.getTriggerMask();
                if ((activeEvent & triggerMask) == triggerMask) {
                    if (getAsyncEnable()) {
                        lock.lock();
                        try {
                            if (consumed.contains(afterEvent.getEventValue())) {
                                continue;
                            }
                            consumed.add(afterEvent.getEventValue());
                        } finally {
                            lock.unlock();
                        }
                    }
                    translate(obj, afterEvent);
                }
            }
        }
    }
}


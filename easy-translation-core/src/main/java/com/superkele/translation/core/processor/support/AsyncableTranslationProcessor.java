package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ObjectUtil;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
        for (Pair<Field, Mapping> pair : mappingFields) {
            FieldTranslationEvent fieldTranslationEvent = new FieldTranslationEvent();
            final short event = (short) (initEvent << leftShift);
            fieldTranslationEvent.setFieldName(pair.getKey().getName());
            fieldTranslationEvent.setEventValue(event);
            fieldTranslationEvent.setAsync(pair.getValue().async());
            fieldTranslationEvent.setFieldTranslationInvoker(getMappingHandler().convert(pair.getKey(), pair.getValue()));
            fieldTranslationMap.put(pair.getKey().getName(), fieldTranslationEvent);
            leftShift++;
        }
        int asyncSize = 0;
        //第二次遍历，划分sort事件和after事件(触发的时机不同，sort事件是按顺序直接触发（一定会），after事件是回调触发（存在不执行的可能）)
        for (Pair<Field, Mapping> pair : mappingFields) {
            String fieldName = pair.getKey().getName();
            Mapping mapping = pair.getValue();
            FieldTranslationEvent fieldTranslationEvent = fieldTranslationMap.get(fieldName);
            if (mapping.after().length == 0) {
                if (mapping.async()) {
                    asyncSize++;
                }
                sortEvents.add(fieldTranslationEvent);
            } else {
                asyncSize++;
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
        res.setAsyncSize(asyncSize);
        return res;
    }

    /**
     * 只能用来处理一次的FieldTranslationHandler
     */
    public class OnceFieldTranslationHandler {
        private final AtomicInteger activeEvent = new AtomicInteger(0);

        private final Set<Short> activeEventMasks = new ConcurrentHashSet<>();

        private final Map<Short, CompletableFuture> futureMap = new ConcurrentHashMap<>();

        private final Map<String, Object> translationResCache = new ConcurrentHashMap<>();

        private final List<ContextPasser> passerCollect = contextHolders.stream()
                .map(ContextPasser::new)
                .collect(Collectors.toList());
        private final Set<Short> eventMaskSet;
        //todo:增加缓存开关
        //  private final boolean cacheEnabled;
        private CountDownLatch latch;

        private boolean used = false;

        private FieldTranslation fieldTranslation;

        public OnceFieldTranslationHandler(FieldTranslation fieldTranslation) {
            this.fieldTranslation = fieldTranslation;
            this.eventMaskSet = fieldTranslation.getAfterEventMaskMap().keySet();
            this.latch = new CountDownLatch(fieldTranslation.getAsyncSize());
        }

        public void handle(Object obj) {
            if (used) {
                throw new IllegalStateException("FieldTranslationHandler只能被调用一次");
            }
            synchronized (this) {
                if (used) {
                    throw new IllegalStateException("FieldTranslationHandler只能被调用一次");
                }
                this.used = true;
            }
            FieldTranslationEvent[] sortEvents = this.fieldTranslation.getSortEvents();
            passerCollect.forEach(ContextPasser::setPassValue);
            //顺序执行事件
            for (FieldTranslationEvent sortEvent : sortEvents) {
                translate(obj, sortEvent);
            }
            if (getAsyncEnable()) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        //如果是after事件，需要阻塞直到前事件执行完毕
        private void translate(Object obj, FieldTranslationEvent event) {
            //如果没开启异步支持
            if (!getAsyncEnable()) {
                translateInternal(obj, event);
                return;
            }
            if (event.getPreEvents() != null && event.getPreEvents().length != 0) {
                short[] preEvents = event.getPreEvents();
                CompletableFuture[] preFuture = new CompletableFuture[preEvents.length];
                for (int i = 0; i < preEvents.length; i++) {
                    preFuture[i] = futureMap.get(preEvents[i]);
                }
                CompletableFuture<Void> completableFuture = CompletableFuture.allOf(preFuture);
                CompletableFuture nextFuture;
                if (event.isAsync()) {
                    nextFuture = completableFuture.thenRunAsync(() -> {
                        passerCollect.forEach(ContextPasser::passContext);
                        translateInternal(obj, event);
                        latch.countDown();
                    }, getThreadPoolExecutor());
                } else {
                    nextFuture = completableFuture.thenRun(() -> {
                        translateInternal(obj, event);
                        latch.countDown();
                    });
                }
                futureMap.put(event.getEventValue(), nextFuture);
                return;
            }
            if (event.isAsync()) {
                //使用上下文同步器传递上下文
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    passerCollect.forEach(ContextPasser::passContext);
                    translateInternal(obj, event);
                    latch.countDown();
                }, getThreadPoolExecutor());
                futureMap.put(event.getEventValue(), future);
            } else {
                translateInternal(obj, event);
            }
        }

        private void translateInternal(Object obj, FieldTranslationEvent event) {
            //获取事件值
            short eventValue = event.getEventValue();
            //获取单个字段翻译器
            FieldTranslationInvoker fieldTranslationInvoker = event.getFieldTranslationInvoker();
            fieldTranslationInvoker.invoke(obj, translationResCache::get, translationResCache::put);
            //更新事件
            this.activeEvent.updateAndGet(current -> current | eventValue);
            //获取事件掩码集合，对比触发after事件
            for (short eventMask : eventMaskSet) {
                if (!this.activeEventMasks.contains(eventMask)) {
                    if ((this.activeEvent.get() & eventMask) == eventMask) {
                        this.activeEventMasks.add(eventMask);
                        FieldTranslationEvent[] fieldTranslationEvents = this.fieldTranslation.getAfterEventMaskMap().get(eventMask);
                        //手动依次触发after事件
                        for (FieldTranslationEvent fieldTranslationEvent : fieldTranslationEvents) {
                            translate(obj, fieldTranslationEvent);
                        }
                    }
                }
            }
        }
    }

}

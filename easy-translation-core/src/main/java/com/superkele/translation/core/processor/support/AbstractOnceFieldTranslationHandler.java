package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.constant.MappingStrategy;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.FieldTranslationHandler;
import com.superkele.translation.core.util.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 该抽象类实现了FieldTranslationHandler接口
 * 具体的工作原理是通过FieldTranslationEvent对象来控制翻译流程
 * 每个event对象中存储了字段的映射策略 strategy,
 * 会根据 strategy 决定调用invokeBatch方法还是invoke方法
 *
 * @see com.superkele.translation.core.mapping.TranslationInvoker#invoke
 * @see com.superkele.translation.core.mapping.TranslationInvoker#invokeBatch
 */
public abstract class AbstractOnceFieldTranslationHandler implements FieldTranslationHandler {

    protected final FieldTranslation fieldTranslation;
    protected final List<Object> sources;
    private final AtomicInteger[] activeEvents;
    private final ReentrantLock[] locks;
    private final Set<Pair<Integer, Short>> consumed = new ConcurrentHashSet<>();
    private final Map<String, Object> cache;

    public AbstractOnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources) {
        this.fieldTranslation = fieldTranslation;
        this.sources = sources;
        this.locks = new ReentrantLock[sources.size()];
        this.activeEvents = new AtomicInteger[sources.size()];
        for (int i = 0; i < this.activeEvents.length; i++) {
            activeEvents[i] = new AtomicInteger(0);
        }
        for (int i = 0; i < this.locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
        if (getCacheEnabled()) {
            this.cache = new ConcurrentHashMap<>();
        } else {
            this.cache = null;
        }
    }

    protected abstract boolean getCacheEnabled();

    protected abstract TranslationInvoker getTranslationInvoker();

    protected abstract Executor getExecutor();

    protected abstract boolean getAsyncEnabled();


    @Override
    public FieldTranslation getFieldTranslation() {
        return fieldTranslation;
    }

    @Override
    public void handle() {
        FieldTranslationEvent[] sortEvents = this.getFieldTranslation().getSortEvents();
        //顺序执行事件
        CompletableFuture[] tasks = Arrays.stream(sortEvents)
                .map(sortEvent -> {
                    MappingStrategy mappingStrategy = sortEvent.getMappingStrategy();
                    if (mappingStrategy == MappingStrategy.BATCH) {
                        translateBatch(sortEvent);
                        return null;
                    } else {
                        CompletableFuture[] futures = new CompletableFuture[sources.size()];
                        for (int i = 0; i < sources.size(); i++) {
                            futures[i] = translateSingle(i, sortEvent);
                        }
                        return futures;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(tasks).join();
    }

    /**
     * 批量翻译
     *
     * @param event
     */
    public void translateBatch(FieldTranslationEvent event) {
        short triggerMask = event.getTriggerMask();
        int totalActived = ~0;
        for (AtomicInteger activeEvent : activeEvents) {
            totalActived &= activeEvent.intValue();
            if ((totalActived & triggerMask) != triggerMask) {
                return;
            }
        }
        getTranslationInvoker().invokeBatch(sources, event, cache);
        for (AtomicInteger activeEvent : activeEvents) {
            activeEvent.updateAndGet(i -> i | event.getEventValue());
        }
        for (int i = 0; i < sources.size(); i++) {
            processHook(i, event);
        }
        for (FieldTranslationEvent activeEvent : event.getActiveEvents()) {
            MappingStrategy mappingStrategy = activeEvent.getMappingStrategy();
            if (mappingStrategy == MappingStrategy.BATCH) {
                translateBatch(activeEvent);
            } else {
                for (int i = 0; i < sources.size(); i++) {
                    if ((activeEvents[i].get() & activeEvent.getTriggerMask()) == activeEvent.getTriggerMask()) {
                        translateSingle(i, activeEvent);
                    }
                }
            }
        }
    }

    /**
     * 执行单体翻译
     *
     * @param sourceIndex source下标
     * @param event 事件
     * @return 异步事件
     */
    public CompletableFuture<Void> translateSingle(int sourceIndex, FieldTranslationEvent event) {
        boolean async = event.isAsync();
        if (async && getAsyncEnabled()) {
            return CompletableFuture.runAsync(() -> {
                        buildAsyncEnv();
                        try {
                            executeTranslate(sourceIndex, event);
                            triggerActiveEvents(sourceIndex, event);
                        } finally {
                            cleanAsyncEnv();
                        }
                    }, getExecutor())
                    .handle((v, e) -> {
                        if (e != null) {
                            throw new TranslationException("异步翻译异常", e);
                        } else {
                            return v;
                        }
                    });
        } else {
            executeTranslate(sourceIndex, event);
            triggerActiveEvents(sourceIndex, event);
            return null;
        }
    }

    /**
     * 进行前置处理，传递环境变量等
     */
    protected abstract void cleanAsyncEnv();

    /**
     * 异步处理结束，消除环境变量等操作
     */
    protected abstract void buildAsyncEnv();

    /**
     * 触发子事件
     *
     * @param sourceIndex source下标
     * @param event 事件
     */
    private void triggerActiveEvents(int sourceIndex, FieldTranslationEvent event) {
        CompletableFuture[] child = Arrays.stream(event.getActiveEvents())
                .filter(activeEvent -> (activeEvents[sourceIndex].get() & activeEvent.getTriggerMask()) == activeEvent.getTriggerMask())
                .filter(activeEvent -> !consumed.contains(Pair.of(sourceIndex, activeEvent.getEventValue())))
                .map(activeEvent -> {
                    MappingStrategy mappingStrategy = activeEvent.getMappingStrategy();
                    if (mappingStrategy == MappingStrategy.BATCH) {
                        translateBatch(activeEvent);
                        return null;
                    } else {
                        return translateSingle(sourceIndex, activeEvent);
                    }
                })
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(child).join();
    }

    /**
     * 执行翻译
     *
     * @param sourceIndex source下标
     * @param event 翻译事件
     */
    public void executeTranslate(int sourceIndex, FieldTranslationEvent event) {
        Pair<Integer, Short> uniqueKey = Pair.of(sourceIndex, event.getEventValue());
        if (consumed.contains(uniqueKey)) {
            return;
        }
        locks[sourceIndex].lock();
        try {
            if (consumed.contains(uniqueKey)) {
                return;
            }
            consumed.add(uniqueKey);
        } finally {
            locks[sourceIndex].unlock();
        }
        if (StrUtil.isNotBlank(event.getTranslator())) {
            getTranslationInvoker().invoke(sources.get(sourceIndex), event, cache);
        }
        activeEvents[sourceIndex].updateAndGet(val -> val | event.getEventValue());
        processHook(sourceIndex, event);
    }

    /**
     * 钩子方法
     *
     * @param sourceIndex source下标
     * @param event 翻译事件
     */
    protected abstract void processHook(int sourceIndex, FieldTranslationEvent event);

}

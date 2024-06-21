package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.mapping.MappingHandler;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.FieldTranslationHandler;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
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
 * 每个event对象中存储了字段的映射策略 MappingHandler,
 * 不同的MappingHandler会根据 #waitPreEventWhenBatch 决定它有没有批处理的能力
 * 具有批处理能力的MappingHandler可以在处理集合时聚合所有对象的mapper参数列表，然后对结果进行映射
 * 不具有批处理能力的MappingHandler在处理集合时，会对每个对象进行单独的翻译，当然，它也具有改变参数形式和结果处理的能力
 *
 * @see com.superkele.translation.core.mapping.MappingHandler
 */
public abstract class AbstractFieldTranslationHandler implements FieldTranslationHandler {

    protected final FieldTranslation fieldTranslation;

    protected final List<Object> sources;

    private final AtomicInteger[] activeEvents;

    private final ReentrantLock[] locks;

    private final Set<Pair<Integer, Short>> consumed = new ConcurrentHashSet<>();

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public AbstractFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources) {
        this.fieldTranslation = fieldTranslation;
        this.sources = sources;
        this.locks = new ReentrantLock[sources.size() + 1];
        this.activeEvents = new AtomicInteger[sources.size()];
        for (int i = 0; i < this.activeEvents.length; i++) {
            activeEvents[i] = new AtomicInteger(0);
        }
        for (int i = 0; i < this.locks.length; i++) {
            locks[i] = new ReentrantLock();
        }
    }

    protected abstract TranslationProcessor getTranslationProcessor();

    protected abstract Executor getExecutorService();

    protected abstract boolean getAsyncEnabled();

    protected abstract TranslatorFactory getTranslatorFactory();

    protected abstract PropertyHandler getPropertyHandler();


    @Override
    public FieldTranslation getFieldTranslation() {
        return fieldTranslation;
    }

    @Override
    public void handle(boolean asyncProcessList) {
        FieldTranslationEvent[] sortEvents = this.getFieldTranslation().getSortEvents();
        //顺序执行事件
        CompletableFuture[] tasks = Arrays.stream(sortEvents)
                .map(sortEvent -> {
                    MappingHandler mappingHandler = sortEvent.getMappingHandler();
                    if (mappingHandler.waitPreEventWhenBatch()) {
                        handleBatch(sortEvent);
                        return null;
                    } else {
                        CompletableFuture[] futures = new CompletableFuture[sources.size()];
                        for (int i = 0; i < sources.size(); i++) {
                            futures[i] = handle(i, sortEvent);
                        }
                        return futures;
                    }
                })
                .filter(Objects::nonNull)
                .flatMap(futures -> Arrays.stream(futures))
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(tasks).join();
    }

    public void handleBatch(FieldTranslationEvent event) {
/*        Pair<Integer, Short> uniqueKey = Pair.of(null, event.getEventValue());
        if (consumed.contains(uniqueKey)) {
            return;
        }
        locks[sources.size()].lock();
        try {
            if (consumed.contains(uniqueKey)) {
                return;
            }
            consumed.add(uniqueKey);
        } finally {
            locks[sources.size()].unlock();
        }*/
        short triggerMask = event.getTriggerMask();
        int totalActived = ~0;
        for (AtomicInteger activeEvent : activeEvents) {
            totalActived &= activeEvent.intValue();
            if ((totalActived & triggerMask) != triggerMask) {
                return;
            }
        }
        event.getMappingHandler().handleBatch(sources, event, getTranslatorFactory().findTranslator(event.getTranslator()), cache);
        for (AtomicInteger activeEvent : activeEvents) {
            activeEvent.updateAndGet(i -> i | event.getEventValue());
        }
        if (event.getRefTranslation() != null) {
            for (int i = 0; i < sources.size(); i++) {
                processHook(i, event);
            }
        }
        for (FieldTranslationEvent activeEvent : event.getActiveEvents()) {
            MappingHandler mappingHandler = activeEvent.getMappingHandler();
            if (mappingHandler.waitPreEventWhenBatch()) {
                handleBatch(activeEvent);
            } else {
                for (int i = 0; i < sources.size(); i++) {
                    if ((activeEvents[i].get() & activeEvent.getTriggerMask()) == activeEvent.getTriggerMask()) {
                        handle(i, activeEvent);
                    }
                }
            }
        }
    }

    public CompletableFuture<Void> handle(int sourceIndex, FieldTranslationEvent event) {
        boolean async = event.isAsync();
        if (async && getAsyncEnabled()) {
            return CompletableFuture.runAsync(() -> {
                        executeTranslate(sourceIndex, event);
                        triggerActiveEvents(sourceIndex, event);
                    }, getExecutorService())
                    .exceptionally(e -> {
                        throw new TranslationException("异步翻译异常", e);
                    });
        } else {
            executeTranslate(sourceIndex, event);
            triggerActiveEvents(sourceIndex, event);
            return null;
        }
    }

    private void triggerActiveEvents(int sourceIndex, FieldTranslationEvent event) {
        CompletableFuture[] child = Arrays.stream(event.getActiveEvents())
                .filter(activeEvent -> (activeEvents[sourceIndex].get() & activeEvent.getTriggerMask()) == activeEvent.getTriggerMask())
                .filter(activeEvent -> !consumed.contains(Pair.of(sourceIndex, activeEvent.getEventValue())))
                .map(activeEvent -> {
                    if (activeEvent.getMappingHandler().waitPreEventWhenBatch()) {
                        handleBatch(activeEvent);
                        return null;
                    } else {
                        return handle(sourceIndex, activeEvent);
                    }
                })
                .filter(Objects::nonNull)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(child).join();
    }

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
            event.getMappingHandler().handle(sources.get(sourceIndex), event, getTranslatorFactory().findTranslator(event.getTranslator()), cache);
        }
        activeEvents[sourceIndex].updateAndGet(val -> val | event.getEventValue());
        processHook(sourceIndex, event);
    }

    protected void processHook(int sourceIndex, FieldTranslationEvent event) {
        if (event.getRefTranslation() != null) {
            getTranslationProcessor().process(getPropertyHandler().invokeGetter(sources.get(sourceIndex), event.getPropertyName()), event.getRefTranslation().type(),
                    event.getRefTranslation().field(), event.isAsync(), event.getRefTranslation().listTypeHandler());
        }
    }

}

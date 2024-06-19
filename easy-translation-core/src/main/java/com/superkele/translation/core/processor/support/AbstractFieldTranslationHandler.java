package com.superkele.translation.core.processor.support;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.mapping.MappingHandler;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.FieldTranslationHandler;
import com.superkele.translation.core.translator.factory.TranslatorFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractFieldTranslationHandler implements FieldTranslationHandler {

    protected final FieldTranslation fieldTranslation;

    protected final List<Object> sources;

    private final AtomicInteger[] activeEvents;

    public AbstractFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources) {
        this.fieldTranslation = fieldTranslation;
        this.sources = sources;
        this.activeEvents = new AtomicInteger[sources.size()];
        for (int i = 0; i < this.activeEvents.length; i++) {
            activeEvents[i] = new AtomicInteger(0);
        }
    }


    protected abstract TranslatorFactory getTranslatorFactory();


    @Override
    public FieldTranslation getFieldTranslation() {
        return fieldTranslation;
    }

    @Override
    public void handle(boolean asyncProcessList) {
        FieldTranslationEvent[] sortEvents = this.fieldTranslation.getSortEvents();
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
        short triggerMask = event.getTriggerMask();
        int totalActived = ~0;
        for (AtomicInteger activeEvent : activeEvents) {
            totalActived &= activeEvent.intValue();
        }
        if ((totalActived & triggerMask) != triggerMask) {
            return;
        }
        event.getMappingHandler().handleBatch(sources, event, getTranslatorFactory().findTranslator(event.getTranslator()));
        for (AtomicInteger activeEvent : activeEvents) {
            activeEvent.updateAndGet(i -> i | event.getEventValue());
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
        if (async) {
            return CompletableFuture.runAsync(() -> {
                execute(sourceIndex, event);
                CompletableFuture[] child = Arrays.stream(event.getActiveEvents())
                        .filter(activeEvent -> (activeEvents[sourceIndex].get() & activeEvent.getTriggerMask()) == activeEvent.getTriggerMask())
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
            });
        } else {
            execute(sourceIndex, event);
            return null;
        }
    }

    public void execute(int sourceIndex, FieldTranslationEvent event) {
        if (StrUtil.isNotBlank(event.getTranslator())) {
            event.getMappingHandler().handle(sources.get(sourceIndex), event, getTranslatorFactory().findTranslator(event.getTranslator()));
        }
        activeEvents[sourceIndex].updateAndGet(val -> val | event.getEventValue());
    }

}

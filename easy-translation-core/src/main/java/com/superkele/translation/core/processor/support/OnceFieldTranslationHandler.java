package com.superkele.translation.core.processor.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.thread.ContextPasser;
import com.superkele.translation.core.translator.factory.TranslatorFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class OnceFieldTranslationHandler extends AbstractFieldTranslationHandler {

    private final AtomicInteger activeEvent = new AtomicInteger(0);
    private final List<ContextPasser> passerCollect = buildContextPasser();
    private final TranslatorFactory translatorFactory;
    private final TranslationProcessor processor;
    private final boolean cacheEnabled;
    private final CountDownLatch latch;
    private Set<Short> consumed = new ConcurrentHashSet<>();
    private Map<String, Object> translationResCache;
    private ReentrantLock lock = new ReentrantLock();

    public OnceFieldTranslationHandler(TranslatorFactory translatorFactory, TranslationProcessor processor, FieldTranslation fieldTranslation) {
        super(fieldTranslation);
        this.translatorFactory = translatorFactory;
        this.processor = processor;
        this.latch = new CountDownLatch(fieldTranslation.getConsumeSize());
        this.cacheEnabled = fieldTranslation.isHasSameInvoker();
        if (this.cacheEnabled) {
            translationResCache = new ConcurrentHashMap<>();
        }
    }

    private static List<ContextPasser> buildContextPasser() {
        return Config.INSTANCE.getContextHolders().stream().map(ContextPasser::new).collect(Collectors.toList());
    }

    @Override
    protected void awaitToTranslation() {
        try {
            latch.await(Config.INSTANCE.getTimeout(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected boolean getAsyncEnable() {
        return Config.INSTANCE.getThreadPoolExecutor() != null;
    }

    @Override
    protected List<ContextPasser> getContextPassers() {
        return passerCollect;
    }



    //如果是after事件，需要阻塞直到前事件执行完毕
    @Override
    protected void translate(Object obj, FieldTranslationEvent event) {
        //如果没开启异步支持
        if (getAsyncEnable()) {
            translateInternal(obj, event);
            return;
        }
        if (event.isAsync()) {
            //使用上下文同步器传递上下文
            CompletableFuture.runAsync(() -> {
                passerCollect.forEach(ContextPasser::passContext);
                translateInternal(obj, event);
                passerCollect.forEach(ContextPasser::clearContext);
            }, Config.INSTANCE.getThreadPoolExecutor());
        } else {
            translateInternal(obj, event);
        }
    }

    private void translateInternal(Object source, FieldTranslationEvent event) {
        //获取事件值
        short eventValue = event.getEventValue();
/*        //获取单个字段翻译器
        MappingHandler mappingHandler = event.getMappingHandler();
        if (cacheEnabled) {
            fieldTranslationInvoker.invoke(source, translationResCache::get, translationResCache::put);
        } else {
            fieldTranslationInvoker.invoke(source);
        }*/
        //如果开启了关联翻译的话
        RefTranslation refTranslation = event.getRefTranslation();
        if (refTranslation != null) {
            //处理相关内容
            processor.process(source, refTranslation.type(),refTranslation.field(), refTranslation.async(), refTranslation.listTypeHandler());
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
                translate(source, afterEvent);
            }
        }
    }
}

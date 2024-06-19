package com.superkele.translation.core.metadata.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.core.annotation.FieldTranslationInvoker;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.metadata.TranslationInvoker;
import com.superkele.translation.core.thread.ContextPasser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractTranslationInvoker implements TranslationInvoker {

    private final FieldTranslation fieldTranslation;
    private final AtomicInteger activeEvent = new AtomicInteger(0);
    private final List<ContextPasser> passerCollect;
    /**
     * 已执行的事件集合
     */
    private final boolean cacheEnabled;
    private final CountDownLatch latch;
    private Set<Short> consumed = new ConcurrentHashSet<>();
    private Map<String, Object> translationResCache;
    private ReentrantLock lock = new ReentrantLock();

    protected AbstractTranslationInvoker(FieldTranslation fieldTranslation) {
        this.fieldTranslation = fieldTranslation;
        this.latch = new CountDownLatch(fieldTranslation.getConsumeSize());
        this.cacheEnabled = fieldTranslation.isHasSameInvoker();
        this.passerCollect = buildPasserCollect();
        if (this.cacheEnabled) {
            translationResCache = new ConcurrentHashMap<>();
        }
    }

    protected abstract List<ContextPasser> buildPasserCollect();

    protected abstract boolean getAsyncEnable();

    protected abstract long getTimeout();

    protected abstract ExecutorService getThreadExecutor();

    @Override
    public FieldTranslation getFieldTranslation() {
        return fieldTranslation;
    }

    @Override
    public void translate(Object obj) {
        if (getAsyncEnable()) {
            passerCollect.forEach(ContextPasser::setPassValue);
        }

    }

    @Override
    public void translateBatch(Collection collect) {

    }


}

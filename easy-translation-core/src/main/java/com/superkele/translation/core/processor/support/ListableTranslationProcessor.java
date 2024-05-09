package com.superkele.translation.core.processor.support;

import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.thread.ContextPasser;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ListableTranslationProcessor extends AsyncableTranslationProcessor {
    @Override
    public void process(Collection<BeanDescription> obj) {
        for (BeanDescription beanDescription : obj) {
            unpackBeanDescription(beanDescription);
        }
    }

    @Override
    public void processAsync(Collection<BeanDescription> obj) {
        if (!getAsyncEnable()) {
            process(obj);
            return;
        }
        List<ContextPasser> contextPassers = buildContextPassers();
        contextPassers.forEach(ContextPasser::passContext);
        CompletableFuture[] futureArr = obj.stream()
                .map(beanDescription -> CompletableFuture.runAsync(() -> unpackBeanDescription(beanDescription), getThreadPoolExecutor()))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(futureArr).join();
    }

    protected void unpackBeanDescription(BeanDescription beanDescription) {
        if (!beanDescription.getClazz().equals(Object.class)) {
            process(beanDescription.getBean(), beanDescription.getClazz());
        } else {
            process(beanDescription.getBean());
        }
    }
}

package com.superkele.translation.core.processor.support;

import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.thread.ContextPasser;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ListableTranslationProcessor extends AsyncableTranslationProcessor {
    @Override
    public void processList(Collection<BeanDescription> obj) {
        for (BeanDescription beanDescription : obj) {
            unpackBeanDescription(beanDescription);
        }
    }

    @Override
    public void processListAsync(Collection<BeanDescription> obj) {
        if (!getAsyncEnable()) {
            processList(obj);
            return;
        }
        List<ContextPasser> contextPassers = buildContextPassers();
        contextPassers.forEach(ContextPasser::setPassValue);
        CompletableFuture[] futureArr = obj.stream()
                .map(beanDescription -> CompletableFuture.runAsync(() -> {
                    contextPassers.forEach(ContextPasser::passContext);
                    unpackBeanDescription(beanDescription);
                    contextPassers.forEach(ContextPasser::clearContext);
                }, getThreadPoolExecutor()))
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

package com.superkele.translation.core.handler.support;

import com.superkele.translation.core.metadata.FieldInfo;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.thread.PrototypeContextExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends CacheableTranslationProcessor {

    protected List<ContextHolder> contextHolders = new ArrayList<>();

    protected abstract ThreadPoolExecutor getThreadPoolExecutor();

    public void addContextHolders(ContextHolder contextHolder) {
        contextHolders.remove(contextHolder);
        contextHolders.add(contextHolder);
    }


/*    @Override
    protected void processFields(Object obj, JobGroup jobGroup) {
        if (getThreadPoolExecutor() != null) {
            processAsync(obj, jobGroup);
        } else {
            processSync(obj, jobGroup);
        }
    }

    protected void processSync(Object obj, JobGroup jobGroup) {
    }

    protected void processAsync(Object obj, JobGroup jobGroup) {
        *//**
         * 主要的性能消耗在于磁盘IO导致的阻塞，所以只对于字段的翻译进行异步处理
         *//*
        List<PrototypeContextExecutor> contextExecutors = contextHolders.stream()
                .map(holder -> new PrototypeContextExecutor(holder))
                .collect(Collectors.toList());
        contextExecutors.forEach(PrototypeContextExecutor::setPassValue);
        //编排异步任务,同一层次的任务可以异步执行，等待同一层次的所有任务执行完毕才会进入下一层
    }*/
}

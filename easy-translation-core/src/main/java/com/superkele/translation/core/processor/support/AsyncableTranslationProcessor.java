package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.metadata.FieldTranslationFactory;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.thread.ContextPasser;
import com.superkele.translation.core.util.PropertyUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 可异步的字段处理器
 */
public abstract class AsyncableTranslationProcessor extends AbstractTranslationProcessor {

    /**
     * key：主类
     * value：处理后的，需要翻译的字段
     */
    private final FieldTranslationFactory fieldTranslationFactory;

    /**
     * 多线程上下文Holder
     */
    private final List<ContextHolder> contextHolders = new CopyOnWriteArrayList<>();

    protected AsyncableTranslationProcessor(FieldTranslationFactory fieldTranslationFactory) {
        this.fieldTranslationFactory = fieldTranslationFactory;
    }

    /**
     * 添加多线程上下文Holder
     */
    public void addContextHolder(ContextHolder contextHolder) {
        contextHolders.remove(contextHolder);
        contextHolders.add(contextHolder);
    }

    /**
     * 添加多线程上下文Holder
     */
    public void addContextHolders(Iterator<ContextHolder> contextHolders) {
        Optional.ofNullable(contextHolders)
                .ifPresent(iterator -> {
                    while (iterator.hasNext()) {
                        ContextHolder contextHolder = iterator.next();
                        this.contextHolders.remove(contextHolder);
                        this.contextHolders.add(contextHolder);
                    }
                });
    }

    @Override
    protected void processInternal(Map<Class, List> classMap, boolean async) {
        if (async && getAsyncEnabled()) {
            CompletableFuture[] futures = classMap.keySet().stream()
                    .map(clazz -> {
                        FieldTranslation fieldTranslation = fieldTranslationFactory.get(clazz, false);
                        OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation, classMap.get(clazz));
                        return CompletableFuture.runAsync(() -> onceFieldTranslationHandler.handle(), getExecutor());
                    })
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(futures).join();
        } else {
            classMap.forEach((clazz, list) -> {
                FieldTranslation fieldTranslation = fieldTranslationFactory.get(clazz, false);
                OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation, list);
                onceFieldTranslationHandler.handle();
            });
        }
    }

    @Override
    protected void processInternal(Object obj, Class<?> clazz) {
        FieldTranslation fieldTranslation = fieldTranslationFactory.get(clazz, false);
        OnceFieldTranslationHandler onceFieldTranslationHandler = new OnceFieldTranslationHandler(fieldTranslation, Collections.singletonList(obj));
        onceFieldTranslationHandler.handle();
    }

    @Override
    protected Boolean predictFilter(Class<?> clazz) {
        return fieldTranslationFactory.get(clazz, false) != null;
    }

    protected List<ContextPasser> buildPassers() {
        return this.contextHolders.stream()
                .map(ContextPasser::new)
                .collect(Collectors.toList());
    }

    protected abstract boolean getAsyncEnabled();

    protected abstract TranslationInvoker getTranslationInvoker();

    protected abstract Executor getExecutor();

    protected abstract boolean getCacheEnabled();

    public class OnceFieldTranslationHandler extends AbstractOnceFieldTranslationHandler {

        private final List<ContextPasser> contextPassers = buildPassers();

        public OnceFieldTranslationHandler(FieldTranslation fieldTranslation, List<Object> sources) {
            super(fieldTranslation, sources);
        }

        @Override
        protected boolean getCacheEnabled() {
            return AsyncableTranslationProcessor.this.getCacheEnabled();
        }

        @Override
        protected TranslationInvoker getTranslationInvoker() {
            return AsyncableTranslationProcessor.this.getTranslationInvoker();
        }

        @Override
        protected Executor getExecutor() {
            return AsyncableTranslationProcessor.this.getExecutor();
        }

        @Override
        protected boolean getAsyncEnabled() {
            return AsyncableTranslationProcessor.this.getAsyncEnabled();
        }

        @Override
        protected void buildAsyncEnv() {
            contextPassers.forEach(ContextPasser::passContext);
        }

        @Override
        protected void cleanAsyncEnv() {
            contextPassers.forEach(ContextPasser::clearContext);
        }


        @Override
        protected void processHook(int sourceIndex, FieldTranslationEvent event) {
            if (event.getRefTranslation() != null) {
                process(PropertyUtils.invokeGetter(sources.get(sourceIndex), event.getPropertyName()), event.getRefTranslation().type(),
                        event.getRefTranslation().field(), event.isAsync(), event.getRefTranslation().listTypeHandler());
            }
        }
    }
}


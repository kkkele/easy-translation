package com.superkele.translation.core.handler.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;

import java.util.concurrent.ThreadPoolExecutor;

public class DefaultTranslatorProcessor extends AsyncableTranslationProcessor {

    private final TransExecutorContext context;

    private ThreadPoolExecutor threadPoolExecutor;

    public DefaultTranslatorProcessor(TransExecutorContext context) {
        this.context = context;
        if (context instanceof DefaultTransExecutorContext defaultContext){
            Config config = defaultContext.getConfig();
            this.threadPoolExecutor = config.getThreadPoolExecutor();
        }
    }

    public DefaultTranslatorProcessor(ThreadPoolExecutor threadPoolExecutor, TransExecutorContext context) {
        this.threadPoolExecutor = threadPoolExecutor;
        this.context = context;
    }

    @Override
    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    @Override
    protected TransExecutorContext getContext() {
        return context;
    }

    @Override
    public void process(Object obj) {

    }
}

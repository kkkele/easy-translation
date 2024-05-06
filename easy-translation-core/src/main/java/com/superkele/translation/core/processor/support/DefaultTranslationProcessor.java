package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.annotation.support.DefaultMappingHandler;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.handle.TranslateExecutor;

import java.util.concurrent.ExecutorService;

public class DefaultTranslationProcessor extends AsyncableTranslationProcessor {

    private final TransExecutorContext context;

    private MappingHandler mappingHandler;


    private Config config;

    public DefaultTranslationProcessor(TransExecutorContext context) {
        this.context = context;
        this.mappingHandler = new DefaultMappingHandler(context);
        if (context instanceof DefaultTransExecutorContext defaultContext) {
            Config config = defaultContext.getConfig();
            this.config = config;
        }
    }


    @Override
    protected ExecutorService getThreadPoolExecutor() {
        return this.config.getThreadPoolExecutor();
    }

    @Override
    protected boolean getAsyncEnable() {
        return this.config.getThreadPoolExecutor() != null;
    }

    @Override
    protected TranslateExecutor getTranslateExecutor(String translatorName) {
        return context.findExecutor(translatorName);
    }

    @Override
    protected MappingHandler getMappingHandler() {
        return mappingHandler;
    }

    @Override
    protected long getTimeout() {
        return config.getTimeout();
    }

}

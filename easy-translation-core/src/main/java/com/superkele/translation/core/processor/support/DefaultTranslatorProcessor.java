package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.annotation.support.DefaultMappingHandler;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.handle.TranslateExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class DefaultTranslatorProcessor extends AsyncableTranslationProcessor {

    private final TransExecutorContext context;

    private MappingHandler mappingHandler;

    private ThreadPoolExecutor threadPoolExecutor;

    public DefaultTranslatorProcessor(TransExecutorContext context) {
        this.context = context;
        this.mappingHandler = new DefaultMappingHandler(context);
        if (context instanceof DefaultTransExecutorContext defaultContext) {
            Config config = defaultContext.getConfig();
            this.threadPoolExecutor = config.getThreadPoolExecutor();
        }
    }

    public DefaultTranslatorProcessor(TransExecutorContext context, MappingHandler mappingHandler) {
        this(context);
        this.mappingHandler = mappingHandler;
    }

    @Override
    protected ThreadPoolExecutor getThreadPoolExecutor() {
        return this.threadPoolExecutor;
    }

    @Override
    protected boolean getAsyncEnable() {
        return this.threadPoolExecutor != null;
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
    protected TransExecutorContext getContext() {
        return context;
    }
}

package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.annotation.support.AbstractMappingHandler;
import com.superkele.translation.core.annotation.support.DefaultMappingHandler;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.property.PropertyGetter;
import com.superkele.translation.core.property.PropertySetter;

import java.util.concurrent.ExecutorService;

public class DefaultTranslationProcessor extends ListableTranslationProcessor {

    private DefaultMappingHandler mappingHandler;

    private Config config;

    public DefaultTranslationProcessor(TransExecutorContext context) {
        this.mappingHandler = new DefaultMappingHandler(context);
        if (context instanceof DefaultTransExecutorContext) {
            Config config = ((DefaultTransExecutorContext) context).getConfig();
            this.config = config;
        }
    }

    public DefaultTranslationProcessor(TransExecutorContext context, Config config) {
        this.mappingHandler = new DefaultMappingHandler(context);
        this.config = config;
    }


    @Override
    protected ExecutorService getThreadPoolExecutor() {
        return this.config.getThreadPoolExecutor();
    }

    public DefaultTranslationProcessor replacePropertyGetter(PropertyGetter propertyGetter) {
        this.mappingHandler = mappingHandler;
        return this;
    }

    public DefaultTranslationProcessor replacePropertySetter(PropertySetter propertySetter) {
        this.mappingHandler = mappingHandler;
        return this;
    }

    @Override
    protected boolean getAsyncEnable() {
        return this.config.getThreadPoolExecutor() != null;
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

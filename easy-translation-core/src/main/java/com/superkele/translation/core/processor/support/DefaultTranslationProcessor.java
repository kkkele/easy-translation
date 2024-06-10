package com.superkele.translation.core.processor.support;

import com.superkele.translation.core.annotation.MappingHandler;
import com.superkele.translation.core.annotation.support.DefaultMappingHandler;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.TranslatorContext;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.property.PropertyGetter;
import com.superkele.translation.core.property.PropertySetter;

import java.util.concurrent.ExecutorService;

public class DefaultTranslationProcessor extends ListableTranslationProcessor {

    private DefaultMappingHandler mappingHandler;

    private Config config;

    public DefaultTranslationProcessor(TranslatorContext context) {
        this.mappingHandler = new DefaultMappingHandler(context, propertyHandler);
        if (context instanceof DefaultTranslatorContext) {
            Config config = ((DefaultTranslatorContext) context).getConfig();
            this.config = config;
        }
    }

    public DefaultTranslationProcessor(TranslatorContext context, Config config) {
        this.mappingHandler = new DefaultMappingHandler(context, propertyHandler);
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

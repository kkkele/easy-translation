package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext{

    private String[] packages;

    private Config config;

    private Object[] invokeObjs;

    public DefaultTranslatorContext(String[] packages, Config config, Object[] invokeObjs) {
        this.packages = packages;
        this.config = config;
        this.invokeObjs = invokeObjs;
        refresh();
    }

    @Override
    protected String[] getLocations() {
        return this.packages;
    }

    @Override
    protected Object[] getRegisterObjs() {
        return this.invokeObjs;
    }

    @Override
    protected Config getConfig() {
        return this.config;
    }
}

package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;

import java.util.Optional;

import static com.superkele.translation.core.constant.TranslationConstant.DEFAULT_PACKAGE;

/**
 * 默认翻译器上下文
 */
public class DefaultTransExecutorContext extends AbstractAutoLoadTransExecutorContext {


    private Config config;

    private Object[] invokeObjs;

    private String[] packages;

    protected DefaultTransExecutorContext() {
    }


    public static DefaultTransExecutorContextBuilder builder() {
        return new DefaultTransExecutorContextBuilder();
    }

    @Override
    public String[] getLocations() {
        return this.packages;
    }

    @Override
    public Object[] getRegisterObjs() {
        return this.invokeObjs;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    public DefaultTransExecutorContext setConfig(Config config) {
        this.config = config;
        return this;
    }

    public DefaultTransExecutorContext setInvokeObjs(Object[] invokeObjs) {
        this.invokeObjs = invokeObjs;
        return this;
    }

    public DefaultTransExecutorContext setPackages(String[] packages) {
        this.packages = packages;
        return this;
    }
}

package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;

/**
 * 默认翻译器上下文
 */
public class DefaultTransExecutorContext extends AbstractAutoLoadTransExecutorContext {


    private Config config;

    private Object[] invokeObjs;

    private String[] packages;

    public DefaultTransExecutorContext(Object... invokeObjs) {
        this(null, invokeObjs);
    }

    public DefaultTransExecutorContext(String... packages) {
        this(new Config(), null, packages);
    }

    public DefaultTransExecutorContext(String[] packages, Object[] invokeObjs) {
        this(new Config(), invokeObjs, packages);
    }

    public DefaultTransExecutorContext(Config config, Object[] invokeObjs, String[] packages) {
        this.config = config;
        this.invokeObjs = invokeObjs;
        this.packages = packages;
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

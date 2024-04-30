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

    DefaultTransExecutorContext() {
    }

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
        this.config = Optional.ofNullable(config).orElse(new Config());
        this.invokeObjs = invokeObjs;
        this.packages = new String[]{DEFAULT_PACKAGE};
        Optional.ofNullable(packages)
                .ifPresent(arr -> {
                    String[] res = new String[arr.length + 1];
                    res[0] = DEFAULT_PACKAGE;
                    for (int i = 0; i < arr.length; i++) {
                        res[i + 1] = arr[i];
                    }
                    this.packages = res;
                });
        refresh();
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

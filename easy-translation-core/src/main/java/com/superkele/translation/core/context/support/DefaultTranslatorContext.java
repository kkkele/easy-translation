package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext {


    private String[] basePackages;

    private InvokeBeanFactory invokeBeanFactory;

    private Config config;

    public DefaultTranslatorContext() {
    }

    public DefaultTranslatorContext(String[] basePackages, InvokeBeanFactory invokeBeanFactory, Config config) {
        this.basePackages = basePackages;
        this.invokeBeanFactory = invokeBeanFactory;
        this.config = config;
    }

    public DefaultTranslatorContext(String[] basePackages, InvokeBeanFactory invokeBeanFactory) {
        this(basePackages, invokeBeanFactory, new Config());
    }

    public DefaultTranslatorContext(String[] basePackages) {
        this(basePackages, null, new Config());
    }

    @Override
    public Config getConfig() {
        return config;
    }

    public DefaultTranslatorContext setConfig(Config config) {
        this.config = config;
        return this;
    }

    @Override
    protected String[] getBasePackages() {
        return basePackages;
    }

    public DefaultTranslatorContext setBasePackages(String[] basePackages) {
        this.basePackages = basePackages;
        return this;
    }

    @Override
    protected InvokeBeanFactory getInvokeBeanFactory() {
        return invokeBeanFactory;
    }

    public DefaultTranslatorContext setInvokeBeanFactory(InvokeBeanFactory invokeBeanFactory) {
        this.invokeBeanFactory = invokeBeanFactory;
        return this;
    }
}

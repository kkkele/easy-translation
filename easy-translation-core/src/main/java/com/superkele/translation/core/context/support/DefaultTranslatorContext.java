package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.invoker.InvokeBeanFactory;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext {


    private String[] basePackages;

    private InvokeBeanFactory invokeBeanFactory;

    public DefaultTranslatorContext() {
    }

    public DefaultTranslatorContext(String[] basePackages, InvokeBeanFactory invokeBeanFactory) {
        this.basePackages = basePackages;
        this.invokeBeanFactory = invokeBeanFactory;
    }


    public DefaultTranslatorContext(String[] basePackages) {
        this(basePackages, null);
    }

    @Override
    public Config getConfig() {
        return Config.INSTANCE;
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

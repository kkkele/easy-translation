package com.superkele.translation.core.context.support;

import com.superkele.translation.core.invoker.InvokeBeanFactory;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext {


    private String[] basePackages;

    private InvokeBeanFactory invokeBeanFactory;


    public DefaultTranslatorContext(InvokeBeanFactory invokeBeanFactory,String... basePackages) {
        this.basePackages = basePackages;
        this.invokeBeanFactory = invokeBeanFactory;
        refresh();
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

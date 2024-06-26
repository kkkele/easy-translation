package com.superkele.translation.core.context.support;

import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.config.DefaultTranslatorNameGenerator;
import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.translator.Translator;

import java.util.Map;

/**
 * 默认翻译器上下文
 */
public class DefaultTranslatorContext extends AbstractAutoLoadTranslatorContext {


    private String[] basePackages;

    private InvokeBeanFactory invokeBeanFactory;

    private Config config;

    public DefaultTranslatorContext(Config config, InvokeBeanFactory invokeBeanFactory, String... basePackages) {
        this.config = config;
        this.basePackages = basePackages;
        this.invokeBeanFactory = invokeBeanFactory;
        refresh();
    }

    public DefaultTranslatorContext(InvokeBeanFactory invokeBeanFactory, String... basePackages) {
        this(new Config(), invokeBeanFactory, basePackages);
    }

    @Override
    protected Map<Integer, Class<? extends Translator>> getTranslatorClazzMap() {
        return config.getTranslatorClazzMap();
    }

    @Override
    protected DefaultTranslatorNameGenerator getTranslatorNameGenerator() {
        return config.getDefaultTranslatorNameGenerator();
    }

    @Override
    protected String[] getBasePackages() {
        return basePackages;
    }

    @Override
    protected InvokeBeanFactory getInvokeBeanFactory() {
        return invokeBeanFactory;
    }
}

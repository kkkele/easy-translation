package com.superkele.translation.core.config;

/**
 * Easy-Translation 全局配置类
 */
public class Config {

    private BeanNameGetter beanNameGetter;

    private DefaultTranslatorGenerator defaultTranslatorGenerator;

    public Config setBeanNameGetter(BeanNameGetter beanNameGetter) {
        this.beanNameGetter = beanNameGetter;
        return this;
    }

    @FunctionalInterface
    interface BeanNameGetter {

        String getDeclaringBeanName(Object invokeObj);

    }

    @FunctionalInterface
    interface DefaultTranslatorGenerator {
        String genName(String clazzName, String methodName);
    }

}

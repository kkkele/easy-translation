package com.superkele.translation.core.config;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Easy-Translation 全局配置类
 */
@Getter
public class Config {

    private BeanNameGetter beanNameGetter = obj -> StrUtil.toCamelCase(obj.getClass().getName());

    private DefaultTranslatorGenerator defaultTranslatorGenerator = (clazzName, methodName) -> StringUtils.join(clazzName, ".", methodName);


    public Config setBeanNameGetter(BeanNameGetter beanNameGetter) {
        this.beanNameGetter = beanNameGetter;
        return this;
    }

    public Config setDefaultTranslatorGenerator(DefaultTranslatorGenerator defaultTranslatorGenerator) {
        this.defaultTranslatorGenerator = defaultTranslatorGenerator;
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

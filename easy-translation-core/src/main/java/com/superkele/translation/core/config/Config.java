package com.superkele.translation.core.config;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Easy-Translation 全局配置类
 */
@Getter
public class Config {

    private BeanNameGetter beanNameGetter = clazz -> StrUtil.toCamelCase(clazz.getSimpleName());

    private DefaultTranslatorNameGenerator defaultTranslatorNameGenerator = (clazzName, methodName) -> StringUtils.join(clazzName, ".", methodName);


    public Config setBeanNameGetter(BeanNameGetter beanNameGetter) {
        this.beanNameGetter = beanNameGetter;
        return this;
    }

    public Config setDefaultTranslatorNameGenerator(DefaultTranslatorNameGenerator defaultTranslatorNameGenerator) {
        this.defaultTranslatorNameGenerator = defaultTranslatorNameGenerator;
        return this;
    }

    @FunctionalInterface
    public interface BeanNameGetter {

        String getDeclaringBeanName(Class clazz);

    }

    @FunctionalInterface
    public interface DefaultTranslatorNameGenerator {
        String genName(String beanName, String methodName);
    }

}

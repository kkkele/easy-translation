package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.BeanNameResolver;

public class DefaultBeanNameResolver implements BeanNameResolver {
    @Override
    public String resolve(String beanName) {
        return beanName;
    }
}

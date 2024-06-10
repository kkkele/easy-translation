package com.superkele.translation.core.invoker;

import com.superkele.translation.annotation.constant.InvokeBeanScope;

public interface InvokeBeanRegistry {

    void register(String beanName, InvokeBeanScope scope, Object bean);
}

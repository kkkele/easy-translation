package com.superkele.translation.core.invoker;

import com.superkele.translation.core.invoker.enums.TransInvokeBeanType;

public interface InvokeBeanRegister {

    void register(String beanName, TransInvokeBeanType beanType, Object bean);
}

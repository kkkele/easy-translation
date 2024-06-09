package com.superkele.translation.core.invoker;

import com.superkele.translation.core.invoker.enums.TranslatorType;

public interface InvokeBeanRegister {

    void register(String beanName, TranslatorType beanType, Object bean);
}

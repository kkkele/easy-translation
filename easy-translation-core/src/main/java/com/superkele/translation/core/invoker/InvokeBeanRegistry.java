package com.superkele.translation.core.invoker;

import com.superkele.translation.core.invoker.enums.TranslatorType;
import com.superkele.translation.core.translator.definition.InvokeBeanScope;

public interface InvokeBeanRegistry {

    void register(String beanName, InvokeBeanScope scope, Object bean);
}

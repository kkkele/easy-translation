package com.superkele.translation.core.invoker.support;

import com.superkele.translation.core.invoker.InvokeBeanRegistry;
import com.superkele.translation.core.translator.definition.InvokeBeanScope;
import com.superkele.translation.core.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class DefaultInvokeBeanRegistry implements InvokeBeanRegistry {

    protected Map<String, Object> singleBeans = new HashMap<>();

    protected Map<String, Pair<Class, Object>> prototypeBeans = new HashMap<>();


    @Override
    public void register(String beanName, InvokeBeanScope scope, Object bean) {
        switch (scope) {
            case SINGLETON:
                singleBeans.put(beanName, bean);
                break;
            case PROTOTYPE:
                prototypeBeans.put(beanName, Pair.of(bean.getClass(), bean));
                break;
        }
    }
}

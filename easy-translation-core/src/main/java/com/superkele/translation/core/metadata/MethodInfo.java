package com.superkele.translation.core.metadata;


import java.lang.reflect.Method;


public abstract class MethodInfo {

    private final Method originMethod;

    private final Object invokeObj;

    public MethodInfo(Method originMethod, Object invokeObj) {
        this.originMethod = originMethod;
        this.invokeObj = invokeObj;
    }

    protected abstract String getDeclaringBeanName();
}

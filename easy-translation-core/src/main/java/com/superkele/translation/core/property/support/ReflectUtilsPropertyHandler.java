package com.superkele.translation.core.property.support;

import com.superkele.translation.core.util.ReflectUtils;

public class ReflectUtilsPropertyHandler implements PropertyHandler {

    @Override
    public Object invokeGetter(Object invokeObj, String propertyName) {
        return ReflectUtils.invokeGetter(invokeObj, propertyName);
    }

    @Override
    public void invokeSetter(Object obj, String propertyName, Object value) {
        ReflectUtils.invokeSetter(obj, propertyName, value);
    }
}

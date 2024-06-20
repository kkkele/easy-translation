package com.superkele.translation.core.util;

import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.core.property.support.DefaultMethodHandlePropertyHandler;

public class PropertyUtils {

    private static volatile PropertyHandler propertyHandler = new DefaultMethodHandlePropertyHandler();

    public static PropertyHandler getPropertyHandler() {
        return propertyHandler;
    }

    public static void setPropertyHandler(PropertyHandler propertyHandler) {
        PropertyUtils.propertyHandler = propertyHandler;
    }

    public static Object invokeGetter(Object invokeObj, String propertyName) {
        return propertyHandler.invokeGetter(invokeObj, propertyName);
    }

    public static void invokeSetter(Object obj, String propertyName, Object value) {
        propertyHandler.invokeSetter(obj, propertyName, value);
    }
}

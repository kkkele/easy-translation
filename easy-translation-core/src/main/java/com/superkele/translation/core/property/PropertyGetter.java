package com.superkele.translation.core.property;

public interface PropertyGetter {

    Object invokeGetter(Object invokeObj, String propertyName);
}

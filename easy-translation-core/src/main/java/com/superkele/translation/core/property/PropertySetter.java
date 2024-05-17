package com.superkele.translation.core.property;

public interface PropertySetter {

    void invokeSetter(Object obj, String propertyName, Object value);
}

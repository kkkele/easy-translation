package com.superkele.translation.core.property.support;

import org.apache.commons.lang3.StringUtils;

public class DefaultMethodHandlePropertyHandler extends AbstractMethodHandlePropertyHandler {


    public static String GETTER_PREFIX = "get";
    public static String SETTER_PREFIX = "set";
    public static String SPLIT_CHAR = ".";

    @Override
    protected String convertToGetterMethodName(String propertyName) {
        return GETTER_PREFIX + StringUtils.capitalize(propertyName);
    }

    @Override
    protected String convertToSetterMethodName(String propertyName) {
        return SETTER_PREFIX + StringUtils.capitalize(propertyName);
    }

    @Override
    protected String[] splitProperty(String propertyName) {
        return StringUtils.split(propertyName, SPLIT_CHAR);
    }
}

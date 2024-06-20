package com.superkele.demo.test.utils;

import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.util.Map;

public class PropertyUtils {

    public static Field[] getFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
        }
        return declaredFields;
    }

    public static Map<String, Object> getProperties(Object obj) {
        Field[] declaredFields = getFields(obj.getClass());
        Map<String, Object> map = Maps.newHashMap();
        for (Field declaredField : declaredFields) {
            try {
                map.put(declaredField.getName(), declaredField.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}

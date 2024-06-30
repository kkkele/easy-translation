package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.UnpackingHandler;
import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultUnpackingHandler implements UnpackingHandler {

    @Override
    public List<BeanDescription> unpackingCollection(Collection collection, Class<?> clazz) {
        if (Object.class.equals(clazz)) {
            return (List<BeanDescription>) collection.stream()
                    .map(obj -> new BeanDescription(obj, obj.getClass()))
                    .collect(Collectors.toList());
        }
        return (List<BeanDescription>) collection.stream()
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }


    @Override
    public List<BeanDescription> unpackingMap(Map map, Class<?> clazz) {
        if (Object.class.equals(clazz)) {
            return (List<BeanDescription>) map.values().stream()
                    .map(obj -> new BeanDescription(obj, obj.getClass()))
                    .collect(Collectors.toList());
        }
        return (List<BeanDescription>) map.values().stream()
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public List<BeanDescription> unpackingArray(Object[] array, Class<?> clazz) {
        if (Object.class.equals(clazz)) {
            return Arrays.stream(array)
                    .map(obj -> new BeanDescription(obj, obj.getClass()))
                    .collect(Collectors.toList());
        }
        return Arrays.stream(array)
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public List<BeanDescription> unpackingOther(Object object, Class<?> clazz) {
        return Collections.emptyList();
    }

    @Override
    public int unpackingType(Object parsingObj) {
        if (parsingObj instanceof Collection) {
            return COLLECTION_TYPE;
        } else if (parsingObj instanceof Map) {
            return MAP_TYPE;
        } else if (parsingObj instanceof Object[]) {
            return ARRAY_TYPE;
        }
        return OBJECT_TYPE;
    }
}

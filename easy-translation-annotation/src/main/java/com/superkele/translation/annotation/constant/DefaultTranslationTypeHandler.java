package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.TranslationUnpackingHandler;
import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultTranslationTypeHandler implements TranslationUnpackingHandler {

    @Override
    public List<BeanDescription> unpackingCollection(Collection collection, Class<?> clazz) {
        return (List<BeanDescription>) collection.stream()
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }


    @Override
    public List<BeanDescription> unpackingMap(Map map, Class<?> clazz) {
        return (List<BeanDescription>) map.values().stream()
                .map(obj -> new BeanDescription(obj, clazz))
                .collect(Collectors.toList());
    }

    @Override
    public List<BeanDescription> unpackingArray(Object[] array, Class<?> clazz) {
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
        if (parsingObj instanceof Collection || parsingObj instanceof Map || parsingObj instanceof Object[]) {
            return 1;
        }
        return 0;
    }
}

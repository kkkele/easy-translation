package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.TranslationListTypeHandler;
import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultTranslationTypeHandler implements TranslationListTypeHandler {

    @Override
    public List<BeanDescription> unpacking(Object collection, Class<?> clazz) {
        if (collection instanceof Collection) {
            Collection collectObj = (Collection) collection;
            if (Object.class.equals(clazz)){
                return (List<BeanDescription>) collectObj.stream()
                        .map(obj -> new BeanDescription(obj, obj.getClass()))
                        .collect(Collectors.toList());
            }else{
                return (List<BeanDescription>) collectObj.stream()
                        .map(obj -> new BeanDescription(obj, clazz))
                        .collect(Collectors.toList());
            }
        }
        return Collections.singletonList(new BeanDescription(collection, clazz));
    }
}

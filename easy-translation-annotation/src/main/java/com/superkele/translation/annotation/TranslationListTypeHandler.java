package com.superkele.translation.annotation;

import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.Collection;
import java.util.List;

public interface TranslationListTypeHandler {

    /**
     * 解包
     */
    List<BeanDescription> unpacking(Object collection, Class<?> clazz);
}

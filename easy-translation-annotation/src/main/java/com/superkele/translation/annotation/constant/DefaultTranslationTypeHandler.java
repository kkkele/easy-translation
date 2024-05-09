package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.TranslationListTypeHandler;
import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultTranslationTypeHandler implements TranslationListTypeHandler {

    @Override
    public List<BeanDescription> unpacking(Object collection, Class<?> clazz) {
        return Collections.emptyList();
    }
}

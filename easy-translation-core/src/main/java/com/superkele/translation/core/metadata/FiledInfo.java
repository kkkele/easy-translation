package com.superkele.translation.core.metadata;

import com.superkele.translation.annotation.constant.ExecuteTiming;
import com.superkele.translation.core.function.TranslationHandler;

import java.util.Map;

/**
 * 字段描述类
 * @param <T>
 */
//todo
public class FiledInfo<T> {

    private final String fieldName;

    private final Class<T> fieldType;

    private final String mapper;

    private final String translator;

    private final String other;

    private final ExecuteTiming executionTiming;

    public FiledInfo(String fieldName, Class<T> fieldType, String mapper, String translator, String other, ExecuteTiming executionTiming) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.mapper = mapper;
        this.translator = translator;
        this.other = other;
        this.executionTiming = executionTiming;
    }
}

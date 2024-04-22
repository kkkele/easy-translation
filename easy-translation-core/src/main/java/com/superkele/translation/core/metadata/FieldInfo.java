package com.superkele.translation.core.metadata;

import com.superkele.translation.annotation.constant.ExecuteTiming;

import java.lang.reflect.Field;

/**
 * 字段描述类
 */
public abstract class FieldInfo {

    private final Field originField;

    protected FieldInfo(Field originField) {
        this.originField = originField;
    }
}

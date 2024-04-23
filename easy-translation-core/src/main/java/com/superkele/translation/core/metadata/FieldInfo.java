package com.superkele.translation.core.metadata;


import com.superkele.translation.annotation.constant.TranslateTiming;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 字段描述类
 */
@Data
public class FieldInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Field originField;

    private String translator;

    private String other;

    private TranslateTiming translateTiming;

    private String receive;

    private String mapper;

}

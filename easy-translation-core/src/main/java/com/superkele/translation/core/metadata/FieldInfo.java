package com.superkele.translation.core.metadata;


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

    private String fieldName;

    private String translator;

    private String[] other;

    private String receive;

    private String[] mapper;

    private boolean notNullMapping;

    private int sort;

    private boolean async;

    private String groupName;
}

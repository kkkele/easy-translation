package com.superkele.translation.core.container;

import com.superkele.translation.core.metadata.FieldInfo;

import java.util.List;

/**
 * @description:
 * <p>
 *     映射字段容器
 * </p>
 */
public interface IMappingFieldContainer {

    /**
     * <p>查找需要映射的字段</p>
     * <hr/>
     * <p>find to be mapped field</p>
     */
    List<FieldInfo> find2bMappedField(Class<?> clazz);
}

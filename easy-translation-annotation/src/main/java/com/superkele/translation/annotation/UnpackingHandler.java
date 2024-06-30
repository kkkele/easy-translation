package com.superkele.translation.annotation;

import com.superkele.translation.annotation.bean.BeanDescription;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 参数解包处理器
 * 用来处理List<List<> Object[] Map<>...等复杂结构
 */
public interface UnpackingHandler {

    /**
     * 解包List
     */
    List<BeanDescription> unpackingCollection(Collection collection, Class<?> clazz);

    /**
     * 解包map
     */
    List<BeanDescription> unpackingMap(Map map, Class<?> clazz);

    /**
     * 解包array
     */
    List<BeanDescription> unpackingArray(Object[] array, Class<?> clazz);

    /**
     * 解包其他类型
     */
    List<BeanDescription> unpackingOther(Object object, Class<?> clazz);

    /**
     * 解析是否需要解包
     * @param obj 解析的参数
     * @return 0：不需要解包
     * 1：需要调用unpackingCollection方法解包
     * 2: 需要调用unpackingMap方法解包
     * 3: 需要调用unpackingArray方法解包
     * 4: 需要调用unpackingOther方法解包
     */
    int unpackingType(Object obj);

    int OBJECT_TYPE = 0;
    int COLLECTION_TYPE = 1;
    int MAP_TYPE = 2;
    int ARRAY_TYPE = 3;
    int OTHER_TYPE = 4;
}

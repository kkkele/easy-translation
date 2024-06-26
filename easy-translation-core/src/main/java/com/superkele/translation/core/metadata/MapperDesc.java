package com.superkele.translation.core.metadata;


import com.superkele.translation.core.mapping.ParamHandler;
import lombok.Data;

@Data
public class MapperDesc {

    /**
     * 映射名
     */
    private String mapper;

    /**
     * 对应映射的值类型
     */
    private Class<?> sourceClass;

    /**
     * 对应翻译器的值类型
     */
    private Class<?> targetClass;

    /**
     * 对应的翻译器的值类型的泛型数组
     */
    private Class<?>[] types;

    /**
     * 参数处理器
     */
    private ParamHandler paramHandler;

}

package com.superkele.translation.core.metadata;


import lombok.Data;

@Data
public class TranslationResDesc {


    /**
     * 返回类型
     */
    private Class<?> targetClass;

    /**
     * 泛型信息
     */
    private Class<?>[] types;
}

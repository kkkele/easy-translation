package com.superkele.translation.core.metadata;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParamDesc {


    /**
     * 参数类型
     */
    private Class<?> targetClass;

    /**
     * 泛型信息
     */
    private Class<?>[] types;
}

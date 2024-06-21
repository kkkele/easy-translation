package com.superkele.translation.core.mapping;

import java.util.function.Function;

/**
 * 属性复制器
 * @param <T> 翻译对象
 * @param <R> 翻译结果对象
 */
public interface PropertyCopier<T,R> {

    /**
     * 将source的某个属性注入到target的指定属性中
     * @param source 翻译结果对象
     * @param receive
     * @param target
     * @param properties
     */
    void copy(R source, String receive, T target, String properties, Function<Throwable,?> handle);

}

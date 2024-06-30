package com.superkele.translation.core.mapping;

import cn.hutool.core.lang.TypeReference;
import com.superkele.translation.core.exception.ParamHandlerException;

import java.lang.reflect.Type;
import java.util.List;

/**
 * 将 翻译中的mapper参数 转化为实际需要的参数
 *
 * @param <S> source，传递的mapper参数
 * @param <T> target，翻译器实际要求的参数类型
 */
public interface ParamHandler<S, T> {

    /**
     * 对单个参数处理
     *
     * @param param       原参数
     * @param targetClazz 目标参数的类
     * @param types       目标参数的泛型
     * @return translator的实际需求参数
     */
    T wrapper(S param, Class<S> sourceClazz, Class<T> targetClazz, Class[] types) throws ParamHandlerException;

    /**
     * 对于集合翻译情况，存在通过列表查询列表的情况
     * 调用该方法，将所有的参数组装成一个列表然后传参
     *
     * @param params      集合内mapper参数对于的值的集合
     * @param targetClazz 目标参数的类
     * @param types       目标参数的泛型
     */
    T wrapperBatch(List<S> params, Class<S> sourceClazz, Class<T> targetClazz, Class[] types) throws ParamHandlerException;
}

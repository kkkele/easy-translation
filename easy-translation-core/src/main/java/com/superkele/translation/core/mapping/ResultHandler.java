package com.superkele.translation.core.mapping;


/**
 * 结果处理器，将结果映射成我们需要的样子并接收
 *
 * @param <T> 原结果
 * @param <R> 处理后的结果
 * @param <S> 接收时传递给翻译对象的结果
 */
public interface ResultHandler<T, R, S> {


    /**
     * 结果处理
     *
     * @param result   翻译的结果
     * @param groupKey 批量翻译时的分组依据
     * @return
     */
    R handle(T result, String[] groupKey);

    /**
     * 结果选择
     *
     * @param processResult #handle 处理后的结果
     * @param index         对象的索引
     * @param mapperKey     对象的映射key数组 例如@Mapping(mapper={"spuId","createTime"})，则会选取spuId和createTime两个属性的值
     * @return
     */
    S map(R processResult, int index,Object source,Object[] mapperKey);
}

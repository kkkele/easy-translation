package com.superkele.translation.annotation;


import com.superkele.translation.annotation.constant.DefaultTranslationTypeHandler;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslationExecute {

    /**
     * 指定是那个类型的
     * @return
     */
    Class<?> type() default Object.class;

    /**
     * 指定翻译某个字段
     */
    String field() default "";

    /**
     * 异步处理翻译(只作用在list返回体中)
     */
    boolean async() default false;

    /**
     * 解包返回体为List的情况 用来应对List<List<T>>,Map 等情况
     */
    Class<? extends TranslationListTypeHandler> listTypeHandler() default DefaultTranslationTypeHandler.class;
}

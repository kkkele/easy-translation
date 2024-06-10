package com.superkele.translation.annotation;

import com.superkele.translation.annotation.constant.DefaultTranslationTypeHandler;

import java.lang.annotation.*;

/**
 * 关联翻译项，如果一个类中的某个属性不能简单的使用@Mapping进行翻译，
 * 而需要使用整体的翻译，则可以使用该注解
 * 使用同 @TranslationExecute
 *
 * @Ducuments https://kkkele.github.io/easy-translation/#/zh-cn/
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RefTranslation {

    /**
     * 指定是那个类型的
     *
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
    Class<? extends TranslationUnpackingHandler> listTypeHandler() default DefaultTranslationTypeHandler.class;


}

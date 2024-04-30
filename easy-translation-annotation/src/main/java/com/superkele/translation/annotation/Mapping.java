package com.superkele.translation.annotation;


import com.superkele.translation.annotation.constant.TranslateTiming;

import java.lang.annotation.*;


/**
 * use the annotation to map the field value
 * support SPEL expression
 * It enables field self-translation,and the value of some fields can be implemented to translate another field
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapping {

    /**
     * 翻译器名称
     */
    String translator() default "";

    /**
     * 映射的字段，spring环境下支持spel表达式
     */
    String[] mapper() default "";

    /**
     * 接收的属性内容
     */
    String receive() default "";

    /**
     * 其他字段
     */
    String[] other() default "";

    /**
     * 执行时机
     */
    TranslateTiming timing() default TranslateTiming.JSON_SERIALIZE;

    /**
     * 当不为null时，是否也映射
     */
    boolean notNullMapping() default false;

    /**
     * 排序字段，按从小到大依次执行，如果值相同，则代表可以并发执行
     */
    int sort() default 0;

    /**
     * 是否异步执行
     */
    boolean async() default false;
}

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
     * Specify a translator, and if you don't specify a translator, automatically look for a translator that matches the type
     */
    String translator() default "";

    /**
     * <p>映射的字段，spring环境下支持spel表达式</p>
     * <hr>
     * <p>Mapped fields, SPEL expressions are supported in spring app</p>
     */
    String[] mapper() default "";

    /**
     * <p>接收的属性内容</p>
     */
    String receive() default "";


    String[] other() default "";

    TranslateTiming timing() default TranslateTiming.JSON_SERIALIZE;

    /**
     * 当不为null时，是否也映射
     */
    boolean notNullMapping() default false;
}

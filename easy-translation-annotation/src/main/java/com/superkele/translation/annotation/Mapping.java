package com.superkele.translation.annotation;


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
     * <p>映射的字段，支持spel表达式</p>
     * <p>Mapped fields, SPEL expressions are supported</p>
     */
    String mapper() default "";

    /**
     * <p>接收的属性内容，支持spel表达式</p>
     * <p>The received property content supports SPEL expressions</p>
     * @return
     */
    String resolver() default "";

    /**
     * Supplementary judgment conditions
     * support SPEL expression
     */
    String other() default "";
}

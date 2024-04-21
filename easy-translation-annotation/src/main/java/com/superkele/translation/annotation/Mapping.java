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
     * mapped from
     */
    String[] mappers() default "";

    /**
     * Supplementary judgment conditions
     * support SPEL expression
     */
    String other() default "";
}

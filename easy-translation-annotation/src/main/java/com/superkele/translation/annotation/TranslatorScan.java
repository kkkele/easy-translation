package com.superkele.translation.annotation;


import java.lang.annotation.*;

/**
 * use the annotation to enable easy-translation
 */
@Inherited
@Target({ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TranslatorScan {

    String[] basePackages() default "";
}

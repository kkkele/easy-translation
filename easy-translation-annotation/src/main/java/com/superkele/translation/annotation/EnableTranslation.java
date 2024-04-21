package com.superkele.translation.annotation;


import java.lang.annotation.*;

/**
 * use the annotation to enable easy-translation
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableTranslation {

    String[] basePackages();
}

package com.superkele.translation.annotation;


import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableMapping {

    boolean enable() default true;

    String condition() default "";
}

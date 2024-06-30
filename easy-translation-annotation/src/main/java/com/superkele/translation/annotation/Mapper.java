package com.superkele.translation.annotation;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {

    String[] value() default "";

    String paramHandler() default "";
}
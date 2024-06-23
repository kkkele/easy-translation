package com.superkele.translation.annotation;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappingHandler {

    String[] groupKey() default {};

    String resultHandler() default "";

    String paramHandler() default "";
}

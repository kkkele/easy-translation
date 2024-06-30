package com.superkele.translation.annotation;


import java.lang.annotation.*;


/**
 * 与@TransMapper配合使用，标注参数或字段为补充条件字段
 */
@Inherited
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransOther {
}

package com.superkele.translation.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * 当使用过在枚举类上的字段上时，标注该字段为翻译返回的值
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransValue {
}

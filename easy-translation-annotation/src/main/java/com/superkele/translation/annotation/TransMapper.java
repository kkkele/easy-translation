package com.superkele.translation.annotation;

import java.lang.annotation.*;

/**
 * @description: 当使用过在枚举类上的字段上时，标注该字段为翻译器的接收映射的字段
 * <hr>
 * 当使用在方法参数上时，标注该参数为翻译器的接收映射的字段，若该方法没有使用@TranslationKey注解，则默认使用第一个参数为接收映射的字段
 */
@Inherited
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TransMapper {
}

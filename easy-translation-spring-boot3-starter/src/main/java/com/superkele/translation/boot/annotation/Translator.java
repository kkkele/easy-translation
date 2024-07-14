package com.superkele.translation.boot.annotation;


import com.superkele.translation.annotation.BeanNameResolver;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.annotation.constant.DefaultBeanNameResolver;
import com.superkele.translation.annotation.constant.InvokeBeanScope;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Translation
public @interface Translator {

    @AliasFor(annotation = Translation.class, attribute = "name")
    String value() default "";


    /**
     * 实现的bean名称
     */
    @AliasFor(annotation = Translation.class, attribute = "invokeBeanName")
    String invokeBeanName() default "";

    /**
     * invokeBeanName解析器
     */
    @AliasFor(annotation = Translation.class, attribute = "beanNameResolver")
    Class<? extends BeanNameResolver> beanNameResolver() default DefaultBeanNameResolver.class;

    /**
     * 原型Bean还是单例Bean
     */
    @AliasFor(annotation = Translation.class, attribute = "scope")
    InvokeBeanScope scope() default InvokeBeanScope.SINGLETON;


}

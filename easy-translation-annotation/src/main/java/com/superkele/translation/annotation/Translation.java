package com.superkele.translation.annotation;


import com.superkele.translation.annotation.constant.DefaultBeanNameResolver;

import java.lang.annotation.*;

/**
 * 用来标记成为翻译器的静态方法，动态方法，枚举类
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Translation {

    /**
     * <p>转换器名称</p>
     * <p>translator name</p>
     */
    String name() default "";

    String invokeBeanName() default "";

    Class<? extends BeanNameResolver> beanNameResolver() default DefaultBeanNameResolver.class;

}

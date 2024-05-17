package com.superkele.translation.annotation;


import java.lang.annotation.*;

/**
 * 用来标记成为翻译器的静态方法，动态方法，枚举类
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translation {

    /**
     * <p>转换器名称</p>
     * <p>translator name</p>
     */
    String name() default "";

}

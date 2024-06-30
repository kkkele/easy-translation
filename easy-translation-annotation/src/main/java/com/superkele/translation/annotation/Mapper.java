package com.superkele.translation.annotation;

import java.lang.annotation.*;


/**
 * 本注解 @Mapper 将以value作为属性名，逐一获取对象的属性
 * 然后使用paramHandler包装成参数，传递给translator，然后执行翻译
 */
@Inherited
@Target(ElementType.TYPE_PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mapper {

    /**
     * 记录属性数组，获取属性后将用ParamHandler进行处理
     * @return
     */
    String[] value() default "";

    /**
     * 参数处理器
     * 使用 全类名 指定该参数的实现类
     * 也可使用 @ + `beanName` 的方式指定
     * @see com.superkele.translation.core.mapping.ParamHandler
     */
    String paramHandler() default "";
}
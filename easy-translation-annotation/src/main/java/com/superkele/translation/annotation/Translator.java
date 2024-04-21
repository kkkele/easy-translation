package com.superkele.translation.annotation;


import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translator {

    /**
     * <p>转换器名称，当value为空时，默认赋值为方法名</p>
     * <p>translator name</p>
     * @return
     */
    String value() default "";

    /**
     * <p>
     *     其他的补充条件，支持 spel表达式
     * </p>
     * <p>
     *     Other complementary conditions support SPEL expressions
     * </p>
     */
    String other() default "";
}

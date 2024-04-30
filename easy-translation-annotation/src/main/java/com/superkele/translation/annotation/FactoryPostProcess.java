package com.superkele.translation.annotation;


import java.lang.annotation.*;

/**
 * 自动扫描，标注这个类是一个TranslatorDefinition装载完成时的后置处理器
 */
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FactoryPostProcess {
}

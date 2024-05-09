package com.superkele.translation.boot.annotation;

import com.superkele.translation.annotation.TranslatorScan;
import com.superkele.translation.boot.config.EnableTranslationImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 用来开启全局翻译注解
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableTranslationImportSelector.class)
@TranslatorScan
public @interface EnableTranslation {

    @AliasFor(annotation = TranslatorScan.class, attribute = "basePackages")
    String[] value() default {};
}

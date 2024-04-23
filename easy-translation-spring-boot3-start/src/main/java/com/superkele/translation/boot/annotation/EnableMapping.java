package com.superkele.translation.boot.annotation;


import com.superkele.translation.annotation.TranslatorScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@TranslatorScan
public @interface EnableMapping {

    @AliasFor(annotation = TranslatorScan.class, attribute = "basePackages")
    String[] value() default "";

    boolean enable() default true;
}

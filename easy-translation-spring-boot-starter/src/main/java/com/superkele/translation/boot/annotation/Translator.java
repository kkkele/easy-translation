package com.superkele.translation.boot.annotation;


import com.superkele.translation.annotation.Translation;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Translation
public @interface Translator {

    @AliasFor(annotation = Translation.class,attribute = "name")
    String value() default "";
}

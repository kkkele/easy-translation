package com.superkele.translation.annotation;


import com.superkele.translation.annotation.constant.TranslateTiming;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translate {

    TranslateTiming timing() default TranslateTiming.AFTER_RETURN;
}

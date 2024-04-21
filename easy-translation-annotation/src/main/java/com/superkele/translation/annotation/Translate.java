package com.superkele.translation.annotation;


import com.superkele.translation.annotation.constant.ExecuteTiming;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translate {

    ExecuteTiming timing() default ExecuteTiming.AFTER_RETURN;
}

package com.superkele.translation.extension.serialize.jackson;

import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@Slf4j
public class JacksonWriteAspect {

    @Around("execution(* com.fasterxml.jackson.databind.ObjectMapper.*(..))")
    public Object aroundObjectMapperMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } finally {
            ConsumedContext.clean();
        }
    }
}

package com.superkele.translation.core.aop;

import com.superkele.translation.annotation.TranslationExecute;
import com.superkele.translation.core.processor.TranslationProcessor;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;



@RequiredArgsConstructor
@Aspect
@Order
public class TranslationAspect {

    private final TranslationProcessor translationProcessor;

    @Around("@annotation(translationExecute)")
    public Object translationExecuteHandler(ProceedingJoinPoint joinPoint, TranslationExecute translationExecute) throws Throwable {
        Object obj = joinPoint.proceed();
        translationProcessor.process(obj, translationExecute.type(), translationExecute.field(), translationExecute.async(), translationExecute.unpackingHandler());
        return obj;
    }

}

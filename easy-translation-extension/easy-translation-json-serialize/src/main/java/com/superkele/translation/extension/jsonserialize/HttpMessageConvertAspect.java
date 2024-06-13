package com.superkele.translation.extension.jsonserialize;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.AutoConfiguration;

@Aspect
@AutoConfiguration
public class HttpMessageConvertAspect {

    @Pointcut("execution(public void org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor.handleReturnValue(..))")
    public void pointCut(){
    }

    @Before("pointCut()")
    public void handleReturnValue(JoinPoint joinPoint){
        joinPoint.getArgs();
    }
}

package com.superkele.translation.core.aop;

import com.superkele.translation.annotation.TranslationExecute;
import com.superkele.translation.annotation.TranslationListTypeHandler;
import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.util.ReflectUtils;
import com.superkele.translation.core.util.TranslationListTypeHandlerUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Aspect
public class TranslationAspect {

    private final TranslationProcessor translationProcessor;

    private Map<Class<? extends TranslationListTypeHandler>, TranslationListTypeHandler> translationListTypeHandlerMap;

    @Around("@annotation(translationExecute)")
    public Object translationExecuteHandler(ProceedingJoinPoint joinPoint, TranslationExecute translationExecute) throws Throwable {
        Object obj = joinPoint.proceed();
        if (obj instanceof Collection) {
            Collection collectionObj = (Collection) obj;
            TranslationListTypeHandler listTypeHandler = TranslationListTypeHandlerUtil.getInstance(translationExecute.listTypeHandler());
            List<BeanDescription> unpacking = listTypeHandler.unpacking(collectionObj, translationExecute.type());
            if (translationExecute.async()) {
                translationProcessor.processAsync(unpacking);
            } else {
                translationProcessor.process(unpacking);
            }
            return obj;
        }
        if (translationExecute.type().equals(Object.class)) {
            translationProcessor.process(ReflectUtils.invokeGetter(obj, translationExecute.field()));
        } else {
            translationProcessor.process(ReflectUtils.invokeGetter(obj, translationExecute.field()), translationExecute.type());
        }
        return obj;
    }

}

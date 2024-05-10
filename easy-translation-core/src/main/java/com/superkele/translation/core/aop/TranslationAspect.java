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
        if (obj == null){
            return null;
        }
        Object targetObj = ReflectUtils.invokeGetter(obj, translationExecute.field());
        if (targetObj instanceof Collection) {
            Collection collectionObj = (Collection) targetObj;
            TranslationListTypeHandler listTypeHandler = TranslationListTypeHandlerUtil.getInstance(translationExecute.listTypeHandler());
            List<BeanDescription> unpacking = listTypeHandler.unpacking(collectionObj, translationExecute.type());
            if (translationExecute.async()) {
                translationProcessor.processListAsync(unpacking);
            } else {
                translationProcessor.processList(unpacking);
            }
            return obj;
        }
        if (translationExecute.type().equals(Object.class)) {
            translationProcessor.process(targetObj);
        } else {
            translationProcessor.process(targetObj, translationExecute.type());
        }
        return obj;
    }

}

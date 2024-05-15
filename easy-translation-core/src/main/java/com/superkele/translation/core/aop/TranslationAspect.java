package com.superkele.translation.core.aop;

import com.superkele.translation.annotation.TranslationExecute;
import com.superkele.translation.annotation.TranslationUnpackingHandler;
import com.superkele.translation.annotation.bean.BeanDescription;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.util.MethodUtils;
import com.superkele.translation.core.util.TranslationListTypeHandlerUtil;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.util.Collection;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Aspect
@Order
public class TranslationAspect {

    private final TranslationProcessor translationProcessor;

    @Around("@annotation(translationExecute)")
    public Object translationExecuteHandler(ProceedingJoinPoint joinPoint, TranslationExecute translationExecute) throws Throwable {
        Object obj = joinPoint.proceed();
        if (obj == null) {
            return null;
        }
        Object targetObj = MethodUtils.invokeGetter(obj, translationExecute.field());
        TranslationUnpackingHandler listTypeHandler = TranslationListTypeHandlerUtil.getInstance(translationExecute.listTypeHandler());
        int unpackingType = listTypeHandler.unpackingType(targetObj);
        if (unpackingType > 0) {
            List<BeanDescription> unpacking = null;
            switch (unpackingType) {
                case 1:
                    unpacking = listTypeHandler.unpackingCollection((Collection) targetObj, translationExecute.type());
                    break;
                case 2:
                    unpacking = listTypeHandler.unpackingMap((Map) targetObj, translationExecute.type());
                    break;
                case 3:
                    unpacking = listTypeHandler.unpackingArray((Object[]) targetObj, translationExecute.type());
                    break;
                case 4:
                    unpacking = listTypeHandler.unpackingOther(targetObj, translationExecute.type());
                    break;
                default:
                    throw new RuntimeException("暂不支持的解包类型");
            }
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

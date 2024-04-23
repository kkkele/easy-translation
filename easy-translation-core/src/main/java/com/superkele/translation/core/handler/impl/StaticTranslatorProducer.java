package com.superkele.translation.core.handler.impl;

import com.superkele.translation.core.function.Translator;
import com.superkele.translation.core.handler.TranslatorProducer;
import com.superkele.translation.core.util.MethodConvert;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

public class StaticTranslatorProducer implements TranslatorProducer {

    /**
     * @param @NotNull method
     * @return
     */
    @Override
    public Translator produce(Method method) throws LambdaConversionException, IllegalAccessException {
        if (!MethodConvert.isStaticMethod(method)) {
            throw new RuntimeException("c.s.t.c.h.i.StaticTranslatorProducer不能处理非静态方法");
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Translator translator = switch (parameterTypes.length) {
            case 0 ->
                    MethodConvert.convertToInterface(Translator.class, "translate", MethodType.methodType(Object.class), method);
            case 1 ->
                    MethodConvert.convertToInterface(Translator.class, "translate", MethodType.methodType(Object.class, Object.class), method);
            case 2 ->
                    MethodConvert.convertToInterface(Translator.class, "translate", MethodType.methodType(Object.class, Object.class, Object.class), method);
            default -> throw new RuntimeException("c.s.t.c.h.i.StaticTranslatorProducer只能处理至多两个参数的方法");
        };
        return translator;
    }


}

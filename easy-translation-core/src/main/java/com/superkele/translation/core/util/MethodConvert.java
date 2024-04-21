package com.superkele.translation.core.util;


import cn.hutool.core.lang.Pair;
import com.superkele.translation.core.function.TranslationHandler;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

public class MethodConvert {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public static <T> T convertToFunctionInterface(Class<T> targetInterface,
                                                   Object convertObject,
                                                   Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        Pair<Method, MethodType> pairs = findMethodType(targetInterface);
        CallSite callSite = LambdaMetafactory.metafactory(
                MethodHandles.lookup(),
                pairs.getKey().getName(),
                MethodType.methodType(targetInterface, convertObject.getClass()),
                pairs.getValue(),
                convertedMethodHandle,
                MethodType.methodType(convertedMethod.getReturnType(), convertedMethod.getParameterTypes()));
        try {
            return (T) callSite.getTarget().invoke(convertObject);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取函数式接口的lambda方法的MethodType
     *
     * @param functionInterface 函数式接口
     * @return
     */
    public static Pair<Method, MethodType> findMethodType(Class<?> functionInterface) {
        Objects.requireNonNull(functionInterface, "target function interface can not be null");
        Method[] methods = functionInterface.getMethods();
        if (functionInterface.isAnnotationPresent(FunctionalInterface.class)) {
            for (Method method : methods) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    return Pair.of(method, MethodType.methodType(method.getReturnType(), method.getParameterTypes()));
                }
            }
        } else {
            Method resultMethod = null;
            int count = 0;
            for (Method method : methods) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    resultMethod = method;
                    count++;
                }
            }
            if (count == 1) {
                return Pair.of(resultMethod, MethodType.methodType(resultMethod.getReturnType(), resultMethod.getParameterTypes()));
            }
        }
        throw new IllegalStateException(functionInterface.getName() + "not function interface");
    }

    public String getById(Long id){
        return "hello:"+id;
    }
}

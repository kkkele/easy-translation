package com.superkele.translation.core.util;


import cn.hutool.core.lang.Pair;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MethodConvert {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, Pair<Method, MethodType>> FUNCTION_INTERFACE_CACHE = new ConcurrentHashMap<>();

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
        return FUNCTION_INTERFACE_CACHE.computeIfAbsent(functionInterface, key -> {
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
            throw new IllegalStateException(functionInterface.getName() + "is not a function interface");
        });
    }
}

package com.superkele.translation.core.convert;


import cn.hutool.core.lang.Pair;
import com.superkele.translation.core.util.Assert;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class MethodConvert {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Map<Class<?>, Pair<Method, MethodType>> FUNCTION_INTERFACE_CACHE = new ConcurrentHashMap<>();

    /**
     * 转换动态方法
     *
     * @param targetInterface
     * @param convertObject
     * @param convertedMethod
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws LambdaConversionException
     */
    public static <T> T convertToFunctionInterface(Class<T> targetInterface,
                                                   Object convertObject,
                                                   Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        Pair<Method, MethodType> pairs = findFunctionInterfaceMethodType(targetInterface);
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
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
     * 转换动态方法
     */
    public static <T> T convertToInterface(Class<T> targetInterface,
                                           String targetMethodName,
                                           MethodType interfaceMethodType,
                                           Object convertObject,
                                           Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
                targetMethodName,
                MethodType.methodType(targetInterface, convertObject.getClass()),
                interfaceMethodType,
                convertedMethodHandle,
                MethodType.methodType(convertedMethod.getReturnType(), convertedMethod.getParameterTypes()));
        try {
            return (T) callSite.getTarget().invoke(convertObject);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换静态方法
     */
    public static <T> T convertToInterface(Class<T> targetInterface,
                                           String targetMethodName,
                                           MethodType interfaceMethodType,
                                           Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
                targetMethodName,
                MethodType.methodType(targetInterface),
                interfaceMethodType,
                convertedMethodHandle,
                MethodType.methodType(convertedMethod.getReturnType(), convertedMethod.getParameterTypes()));
        try {
            return (T) callSite.getTarget().invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转换静态方法
     */
    public static <T> T convertToFunctionInterface(Class<T> targetInterface,
                                                   Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        Pair<Method, MethodType> pairs = findFunctionInterfaceMethodType(targetInterface);
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
                pairs.getKey().getName(),
                MethodType.methodType(targetInterface),
                pairs.getValue(),
                convertedMethodHandle,
                MethodType.methodType(convertedMethod.getReturnType(), convertedMethod.getParameterTypes()));
        try {
            return (T) callSite.getTarget().invoke();
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
    public static Pair<Method, MethodType> findFunctionInterfaceMethodType(Class<?> functionInterface) {
        return FUNCTION_INTERFACE_CACHE.computeIfAbsent(functionInterface, key -> {
            Assert.notNull(functionInterface, "target function interface can not be null");
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

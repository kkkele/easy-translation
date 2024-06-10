package com.superkele.translation.core.convert;


import com.superkele.translation.core.util.Pair;

import java.lang.invoke.*;
import java.lang.reflect.Method;

import static com.superkele.translation.core.util.ReflectUtils.findFunctionInterfaceMethodType;

public class MethodConvert {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

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
     * 将方法转为任意接口句柄
     */
    public static MethodHandle getMethodHandle(Class<?> targetInterface,
                                               String targetMethodName,
                                               MethodType interfaceMethodType,
                                               Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
                targetMethodName,
                MethodType.methodType(targetInterface, convertedMethod.getDeclaringClass()),
                interfaceMethodType,
                convertedMethodHandle,
                MethodType.methodType(convertedMethod.getReturnType(), convertedMethod.getParameterTypes()));
        return callSite.getTarget();
    }

    /**
     * 将动态方法转为任意FunctionInterface句柄
     */
    public static MethodHandle getDynamicMethodHandle(Class<?> targetInterface,
                                        Method convertedMethod)
            throws IllegalAccessException, LambdaConversionException {
/*        Pair<Method, MethodType> pairs = findFunctionInterfaceMethodType(targetInterface);
        return getMethodHandle(targetInterface, pairs.getKey().getName(), pairs.getValue(), convertedMethod);*/
        MethodHandle convertedMethodHandle = LOOKUP.unreflect(convertedMethod);
        Pair<Method, MethodType> pairs = findFunctionInterfaceMethodType(targetInterface);
        CallSite callSite = LambdaMetafactory.metafactory(
                LOOKUP,
                pairs.getKey().getName(),
                MethodType.methodType(targetInterface, convertedMethod.getDeclaringClass()),
                pairs.getValue(),
                convertedMethodHandle,
                MethodType.methodType(convertedMethod.getReturnType(), convertedMethod.getParameterTypes()));
        return callSite.getTarget();
    }

    /**
     * 将静态方法转为任意FunctionInterface句柄
     */
    public static MethodHandle getStaticMethodHandle(Class<?> targetInterface,
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
        return callSite.getTarget();
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


}

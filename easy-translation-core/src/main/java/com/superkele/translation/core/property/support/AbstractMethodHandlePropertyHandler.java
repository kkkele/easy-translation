package com.superkele.translation.core.property.support;

import cn.hutool.core.map.WeakConcurrentMap;
import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.property.Getter;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.util.Map;

public abstract class AbstractMethodHandlePropertyHandler implements PropertyHandler {

    private final Map<Pair<Class<?>, String>, MethodHandle> getterMethodHandleCache = new WeakConcurrentMap<>();

    private final Map<Pair<Class<?>, String>, MethodHandle[]> propertyGetterCache = new WeakConcurrentMap<>();

    // private final Map<Pair<Class<?>, String>, MethodHandle> setterMethodHandleCache = new WeakConcurrentMap<>();


    protected abstract String convertToGetterMethodName(String propertyName);

    protected abstract String convertToSetterMethodName(String propertyName);

    protected abstract String[] splitProperty(String propertyName);

    @Override
    public Object invokeGetter(Object invokeObj, String propertyName) {
        MethodHandle[] methodHandlesArr = propertyGetterCache.get(Pair.of(invokeObj.getClass(), propertyName));
        if (methodHandlesArr != null) {
            Object res = invokeObj;
            for (MethodHandle methodHandle : methodHandlesArr) {
                try {
                    Getter getter = (Getter) methodHandle.invoke(res);
                    res = getter.get();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
            return res;
        }
        Object res = invokeObj;
        String[] properties = splitProperty(propertyName);
        MethodHandle[] methodHandles = new MethodHandle[properties.length];
        for (int i = 0; i < methodHandles.length; i++) {
            MethodHandle methodHandle = generateGetterMethodHandle(res, properties[i]);
            methodHandles[i] = methodHandle;
            try {
                Getter getter = (Getter) methodHandle.invoke(res);
                res = getter.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        propertyGetterCache.put(Pair.of(invokeObj.getClass(), propertyName), methodHandles);
        return res;
    }

    @Override
    public void invokeSetter(Object invokeObj, String propertyName, Object value) {
        Assert.isTrue(StringUtils.isNotBlank(propertyName), "propertyName must not be blank");
        ReflectUtils.invokeSetter(invokeObj, propertyName, value);
    }

    protected MethodHandle generateGetterMethodHandle(Object temp, String property) {
        return getterMethodHandleCache.computeIfAbsent(Pair.of(temp.getClass(), property), methodName -> {
            try {
                String getterMethodName = convertToGetterMethodName(property);
                return MethodConvert.getDynamicMethodHandle(Getter.class, temp.getClass().getMethod(getterMethodName));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (LambdaConversionException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }

}

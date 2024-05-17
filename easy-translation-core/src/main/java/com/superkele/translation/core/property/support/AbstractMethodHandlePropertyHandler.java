package com.superkele.translation.core.property.support;

import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.property.Getter;
import com.superkele.translation.core.property.Setter;
import com.superkele.translation.core.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMethodHandlePropertyHandler implements PropertyHandler {

    private final Map<Pair<Class<?>, String>, MethodHandle> getterMethodHandleCache = new ConcurrentHashMap<>();

    private final Map<Pair<Class<?>, String>, MethodHandle[]> propertyGetterCache = new ConcurrentHashMap<>();

    private final Map<String, MethodHandle> setterMethodHandleCache = new ConcurrentHashMap<>();


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
/*        Object res = invokeObj;
        String[] properties = StringUtils.split(propertyName, ".");
        for (int i = 0; i < properties.length - 1; i++) {
            String getterMethodName = convertToGetterMethodName(properties[i]);
            final Object temp = res;
            MethodHandle methodHandle = getterMethodHandleCache.computeIfAbsent(getterMethodName, methodName -> {
                try {
                    return MethodConvert.getMethodHandle(Getter.class, temp.getClass().getMethod(getterMethodName));
                } catch (LambdaConversionException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                Getter getter = (Getter) methodHandle.invoke(res);
                res = getter.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        final Object temp = res;
        String setterMethodName = convertToSetterMethodName(properties[properties.length - 1]);
        MethodHandle setterMethodHandle = getterMethodHandleCache.computeIfAbsent(setterMethodName, methodName -> {
            try {
                return MethodConvert.getMethodHandle(Setter.class, temp.getClass().getMethod(setterMethodName));
            } catch (LambdaConversionException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            Setter setter = (Setter) setterMethodHandle.invoke(res, value);
            setter.set(value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }*/
    }

    protected MethodHandle generateGetterMethodHandle(Object temp, String property) {
        return getterMethodHandleCache.computeIfAbsent(Pair.of(temp.getClass(), property), methodName -> {
            try {
                String getterMethodName = convertToGetterMethodName(property);
                return MethodConvert.getMethodHandle(Getter.class, temp.getClass().getMethod(getterMethodName));
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

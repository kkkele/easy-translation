package com.superkele.translation.core.property.support;

import cn.hutool.core.map.WeakConcurrentMap;
import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.property.PropertyGetter;
import com.superkele.translation.core.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.util.Map;

public abstract class AbstractMethodHandlePropertyHandler implements PropertyHandler {

    private final Map<Pair<Class<?>, String>, MethodHandle> methodHandleMap = new WeakConcurrentMap<>();

    protected abstract String convertToGetterMethodName(String propertyName);

    protected abstract String convertToSetterMethodName(String propertyName);

    protected abstract String[] splitProperty(String propertyName);

    @Override
    public Object invokeGetter(Object invokeObj, String propertyName) {
        Object res = invokeObj;
        String[] properties = splitProperty(propertyName);
        for (String property : properties) {
            String getterMethodName = convertToGetterMethodName(property);
            MethodHandle methodHandle = methodHandleMap.computeIfAbsent(Pair.of(res.getClass(), getterMethodName), pair -> {
                try {
                    return MethodConvert.getMethodHandle(Getter.class, pair.getKey().getMethod(pair.getValue()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (LambdaConversionException e) {
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
        return res;
    }

    @Override
    public void invokeSetter(Object invokeObj, String propertyName, Object value) {
        Object res = invokeObj;
        String[] properties = StringUtils.split(propertyName, ".");
        for (int i = 0; i < properties.length - 1; i++) {
            String setterMethodName = convertToSetterMethodName(properties[i]);
            MethodHandle methodHandle = methodHandleMap.computeIfAbsent(Pair.of(res.getClass(), setterMethodName), pair -> {
                try {
                    return MethodConvert.getMethodHandle(Getter.class, pair.getKey().getMethod(pair.getValue()));
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
        MethodHandle setterMethodHandle = methodHandleMap.computeIfAbsent(Pair.of(res.getClass(), convertToSetterMethodName(properties[properties.length - 1])), pair -> {
            try {
                return MethodConvert.getMethodHandle(Setter.class, pair.getKey().getMethod(pair.getValue(), Object.class));
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
        }
    }


    interface Getter {
        Object get();
    }

    interface Setter {
        void set(Object value);
    }
}

package com.superkele.translation.core.property.support;

import cn.hutool.core.map.WeakConcurrentMap;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.property.PropertyHandler;
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

    private static final Map<Pair<Class<?>, String>, MethodHandle> GETTER_METHOD_HANDLE_CACHE = new WeakConcurrentMap<>();

    private static final Map<Pair<Class<?>, String>, MethodHandle[]> PROPERTY_GETTER_CACHE = new WeakConcurrentMap<>();

    protected abstract String convertToGetterMethodName(String propertyName);

    protected abstract String convertToSetterMethodName(String propertyName);

    protected abstract String[] splitProperty(String propertyName);

    @Override
    public Object invokeGetter(Object invokeObj, String propertyName) {
        MethodHandle[] methodHandlesArr = PROPERTY_GETTER_CACHE.get(Pair.of(invokeObj.getClass(), propertyName));
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
        PROPERTY_GETTER_CACHE.put(Pair.of(invokeObj.getClass(), propertyName), methodHandles);
        return res;
    }

    @Override
    public void invokeSetter(Object invokeObj, String propertyName, Object value) {
        Assert.isTrue(StringUtils.isNotBlank(propertyName), "propertyName must not be blank");
        ReflectUtils.invokeSetter(invokeObj, propertyName, value);
    }

    protected MethodHandle generateGetterMethodHandle(Object temp, String property) {
        Class<?> clazz = temp.getClass();
        return GETTER_METHOD_HANDLE_CACHE.computeIfAbsent(Pair.of(clazz, property), methodName -> {
            String getterMethodName = convertToGetterMethodName(property);
            try {
                return MethodConvert.getDynamicMethodHandle(Getter.class, clazz.getMethod(getterMethodName));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (LambdaConversionException e) {
                throw new TranslationException("请仔细检查是否存在该方法名"+getterMethodName+"，静态方法是否与@Data生成的方法冲突，（同一类下方法名一致，且参数相同）",e);
            } catch (NoSuchMethodException e) {
                throw new TranslationException("调用get方法获取属性时失败，方法["+clazz+"#"+getterMethodName+"]不存在",e);
            }
        });
    }

}

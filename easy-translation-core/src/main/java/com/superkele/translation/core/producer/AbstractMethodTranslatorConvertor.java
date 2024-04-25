package com.superkele.translation.core.producer;

import com.superkele.translation.core.metadata.Translator;
import com.superkele.translation.core.util.MethodConvert;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMethodTranslatorConvertor implements StaticTranslatorConvertor, VirtualTranslatorConvertor {

    public abstract List<Translator> convertVirtualMethod(Object invokeObj);

    public abstract List<Translator> convertStaticMethod(Class<?> clazz);

    public abstract List<Translator> convertAllMethod(Object invokeObj);


    @Override
    public Translator convert(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return MethodConvert.convertToInterface(Translator.class, "translate", getTranslatorMethodType(parameterTypes), clazz.getDeclaredMethod(methodName, parameterTypes));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Translator convert(Object invokeObj, Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return MethodConvert.convertToInterface(Translator.class, "translate", getTranslatorMethodType(parameterTypes), invokeObj, clazz.getDeclaredMethod(methodName, parameterTypes));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }


    private MethodType getTranslatorMethodType(Class<?>... parameterTypes) {
        List<Class<?>> pTypeList = new ArrayList<>();
        for (int i = 0; i < parameterTypes.length; i++) {
            pTypeList.add(Object.class);
        }
        return MethodType.methodType(Object.class, pTypeList);
    }
}

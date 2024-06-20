package com.superkele.translation.core.translator.support;


import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.core.decorator.TranslatorDecorator;
import com.superkele.translation.core.translator.MapperTranslator;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.translator.factory.AutowireCapableTranslatorFactory;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.ReflectUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public abstract class AbstractAutowireCapableTranslatorFactory extends AbstractTranslatorFactory
        implements AutowireCapableTranslatorFactory {

    @Override
    public Translator createTranslator(String translatorName, TranslatorDefinition definition) {
        TranslatorDecorator translateDecorator = definition.getTranslateDecorator();
        Translator origin = null;
        // 根据beanType选择不同的创建方式
        switch (definition.getTranslatorType()) {
            case DYNAMIC_METHOD:
                Object translatorInvoker = Optional.ofNullable(getBeanInvoker(definition.getInvokeBeanName()))
                        .orElse(getBeanInvoker(definition.getInvokeBeanClazz()));
                Assert.notNull(translatorInvoker, "translator:[" + translatorName + "],{the invokerBean name[" + definition.getInvokeBeanName() + "],type["+definition.getInvokeBeanClazz().getSimpleName()+"]} is not found");
                origin = createTranslator(translatorInvoker, definition.getMethodHandle());
                break;
            case STATIC_METHOD:
                origin = createTranslator(definition.getMethodHandle());
                break;
            case ENUM:
                origin = createTranslator((Class<? extends Enum>) definition.getInvokeBeanClazz());
                break;
        }
        Assert.notNull(origin, "createTranslator failed");
        Translator decorate = translateDecorator.decorate(origin);
        Translator processBeforeInit = applyTranslatorPostProcessorBeforeInit(decorate, translatorName);
        Assert.notNull(processBeforeInit, "applyTranslatorPostProcessorBeforeInit make translator to be null");
        Translator processorAfterInit = applyTranslatorPostProcessorAfterInit(processBeforeInit, translatorName);
        Assert.notNull(processorAfterInit, "applyTranslatorPostProcessorAfterInit make translator to be null");
        return processorAfterInit;
    }

    @Override
    public Translator applyTranslatorPostProcessorBeforeInit(Translator translator, String translatorName) {
        Translator result = translator;
        for (TranslatorPostProcessor translatorPostProcessor : getTranslatorPostProcessors()) {
            result = translatorPostProcessor.postProcessorBeforeInit(result, translatorName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    @Override
    public Translator applyTranslatorPostProcessorAfterInit(Translator translator, String translatorName) {
        Translator result = translator;
        for (TranslatorPostProcessor translatorPostProcessor : getTranslatorPostProcessors()) {
            result = translatorPostProcessor.postProcessorAfterInit(result, translatorName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    protected abstract Object getBeanInvoker(String beanName);

    protected abstract <T> T getBeanInvoker(Class<T> clazz);

    /**
     * 将动态方法转为Translator
     *
     * @param methodHandle
     * @return
     */
    public Translator createTranslator(Object obj, MethodHandle methodHandle) {
        try {
            return (Translator) methodHandle.invoke(obj);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将静态方法转为Translator
     *
     * @param methodHandle
     * @return
     */
    public Translator createTranslator(MethodHandle methodHandle) {
        try {
            return (Translator) methodHandle.invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将枚举转为Translator
     *
     * @param enumClass
     * @return
     */
    public Translator createTranslator(Class<? extends Enum> enumClass) {
        Field[] declaredFields = enumClass.getDeclaredFields();
        int mapperIndex = -1;
        int translationIndex = -1;
        for (int i = 0; i < declaredFields.length; i++) {
            if (declaredFields[i].isAnnotationPresent(TransMapper.class)) {
                Assert.isTrue(mapperIndex == -1, "Enum translator must have only one mapper field");
                mapperIndex = i;
            }
            if (declaredFields[i].isAnnotationPresent(TransValue.class)) {
                Assert.isTrue(translationIndex == -1, "Enum translator must have only one value field");
                translationIndex = i;
            }
        }
        Map<Object, Object> map = new HashMap<>();
        for (Enum enumConstant : enumClass.getEnumConstants()) {
            map.put(ReflectUtils.invokeGetter(enumConstant, declaredFields[mapperIndex].getName()), ReflectUtils.invokeGetter(enumConstant, declaredFields[translationIndex].getName()));
        }
        MapperTranslator mapperTranslator = mapper -> map.get(mapper);
        return mapperTranslator;
    }

}

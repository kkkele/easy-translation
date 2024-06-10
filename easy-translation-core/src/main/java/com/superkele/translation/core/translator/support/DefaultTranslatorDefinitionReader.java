package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.BeanNameResolver;
import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.invoker.enums.TranslatorType;
import com.superkele.translation.core.translator.MapperTranslator;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.util.Assert;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.*;
import java.util.*;

public class DefaultTranslatorDefinitionReader extends AbstractTranslatorDefinitionReader {

    private final Map<Class<? extends BeanNameResolver>, BeanNameResolver> singleton = new HashMap<>();
    private Config config = new Config();

    public DefaultTranslatorDefinitionReader(TranslatorDefinitionRegistry registry) {
        super(registry);
    }

    public DefaultTranslatorDefinitionReader setConfig(Config config) {
        this.config = config;
        return this;
    }


    @Override
    protected String getDefaultTranslatorName(Method method) {
        String beanName = config.getBeanNameGetter().getDeclaringBeanName(method.getDeclaringClass());
        return config.getDefaultTranslatorNameGenerator().genName(beanName, method.getName());
    }

    @Override
    protected String getDefaultTranslatorName(Class<?> clazz) {
        return config.getBeanNameGetter().getDeclaringBeanName(clazz);
    }

    /**
     * 枚举翻译暂且只支持一个mapper参数
     *
     * @param enumClass
     * @return
     */
    @Override
    protected TranslatorDefinition convertEnumToTranslatorDefinition(Class<? extends Enum> enumClass) {
        Field[] declaredFields = enumClass.getDeclaredFields();
        Assert.isTrue(declaredFields.length > 0, "Enum translator must have at least one field");
        int mapperIndex = -1;
        Field mappedField = null;
        for (int i = 0; i < declaredFields.length; i++) {
            if (declaredFields[i].isAnnotationPresent(TransMapper.class)) {
                Assert.isTrue(mapperIndex == -1, "Enum translator must have only one mapper field");
                mapperIndex = i;
            }
            if (declaredFields[i].isAnnotationPresent(TransValue.class)) {
                Assert.isTrue(mappedField == null, "Enum translator must have only one value field");
                mappedField = declaredFields[i];
            }
        }
        Assert.notNull(mappedField, "Enum translator must have one and only one value field");
        Assert.isTrue(mapperIndex != -1, "Enum translator must have  one and only one  mapper field");
        TranslatorDefinition translatorDefinition = new TranslatorDefinition();
        translatorDefinition.setTranslatorType(TranslatorType.ENUM);
        translatorDefinition.setInvokeBeanClazz(enumClass);
        translatorDefinition.setReturnType(mappedField.getType());
        translatorDefinition.setParameterTypes(new Class[]{declaredFields[mapperIndex].getType()});
        translatorDefinition.setTranslatorClass(MapperTranslator.class);
        translatorDefinition.setTranslateDecorator(x -> x);
        translatorDefinition.setMapperIndex(new int[1]);
        return translatorDefinition;
    }

    @Override
    protected TranslatorDefinition convertStaticMethodToTranslatorDefinition(Class<?> clazz, Method method) {
        Class<? extends Translator> translatorClazz = config.getTranslatorClazzMap().get(method.getParameterCount());
        if (translatorClazz == null) {
            throw new TranslationException("Do not find the translator type with " + method.getParameterCount() + "params ,see https://kkkele.github.io/easy-translation/#/zh-cn/config/ for more information");
        }
        MethodHandle methodHandle;
        try {
            methodHandle = MethodConvert.getStaticMethodHandle(translatorClazz, method);
        } catch (IllegalAccessException e) {
            throw new TranslationException("EasyTranslator:" + e);
        } catch (LambdaConversionException e) {
            throw new TranslationException("EasyTranslator:" + e);
        }
        TranslatorDefinition definition = new TranslatorDefinition();
        definition.setMethodHandle(methodHandle);
        definition.setReturnType(method.getReturnType());
        definition.setParameterTypes(method.getParameterTypes());
        definition.setTranslatorClass(translatorClazz);
        definition.setMapperIndex(getIndexPair(method));
        definition.setTranslatorType(TranslatorType.STATIC_METHOD);
        definition.setTranslateDecorator(x -> x);
        definition.setInvokeBeanClazz(clazz);
        return definition;
    }


    @Override
    protected TranslatorDefinition convertDynamicMethodToTranslatorDefinition(Class<?> clazz, Method method, Translation translation) {
        Class<? extends Translator> translatorClazz = config.getTranslatorClazzMap().get(method.getParameterCount());
        if (translatorClazz == null) {
            throw new TranslationException("Do not find the translator type with " + method.getParameterCount() + " params ,see https://kkkele.github.io/easy-translation/#/zh-cn/config/ for more information");
        }
        MethodHandle methodHandle;
        try {
            methodHandle = MethodConvert.getDynamicMethodHandle(translatorClazz, method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        }
        BeanNameResolver beanNameResolver = singleton.computeIfAbsent(translation.beanNameResolver(), key -> {
            try {
                Constructor<? extends BeanNameResolver> constructor = key.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        TranslatorDefinition definition = new TranslatorDefinition();
        definition.setReturnType(method.getReturnType());
        definition.setParameterTypes(method.getParameterTypes());
        definition.setTranslatorClass(translatorClazz);
        definition.setMapperIndex(getIndexPair(method));
        definition.setMethodHandle(methodHandle);
        definition.setTranslatorType(TranslatorType.DYNAMIC_METHOD);
        definition.setTranslateDecorator(x -> x);
        definition.setInvokeBeanClazz(clazz);
        definition.setInvokeBeanName(beanNameResolver.resolve(translation.invokeBeanName()));
        return definition;
    }

    public int[] getIndexPair(Method method) {
        List<Integer> mapperIndexList = new LinkedList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(TransMapper.class)) {
                mapperIndexList.add(i);
            }
        }
        //如果没有直接标注，则默认第一个参数为mapper,其他为other补充字段
        if (mapperIndexList.size() == 0 && parameters.length >= 1) {
            return new int[1];
        }
        return mapperIndexList.stream().mapToInt(Integer::intValue).toArray();
    }


}

package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.*;
import com.superkele.translation.core.config.DefaultTranslatorNameGenerator;
import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.invoker.enums.TranslatorType;
import com.superkele.translation.core.metadata.ParamDesc;
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

    private final DefaultTranslatorNameGenerator defaultTranslatorNameGenerator;

    private final Map<Integer, Class<? extends Translator>> translatorClazzMap;

    public DefaultTranslatorDefinitionReader(TranslatorDefinitionRegistry registry, DefaultTranslatorNameGenerator defaultTranslatorNameGenerator, Map<Integer, Class<? extends Translator>> translatorClazzMap) {
        super(registry);
        this.defaultTranslatorNameGenerator = defaultTranslatorNameGenerator;
        this.translatorClazzMap = translatorClazzMap;
    }


    @Override
    protected String getDefaultTranslatorName(Method method) {
        return defaultTranslatorNameGenerator.genName(method.getDeclaringClass(), method);
    }

    @Override
    protected String getDefaultTranslatorName(Class<?> clazz) {
        return defaultTranslatorNameGenerator.genName(clazz, null);
    }

    /**
     * 枚举翻译暂且只支持一个mapper参数
     * @param enumClass 枚举类
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
        translatorDefinition.setParameterTypes(new ParamDesc[]{new ParamDesc(declaredFields[mapperIndex].getType(), null)});
        translatorDefinition.setTranslatorClass(MapperTranslator.class);
        translatorDefinition.setTranslateDecorator(x -> x);
        translatorDefinition.setMapperIndex(new int[1]);
        return translatorDefinition;
    }

    @Override
    protected TranslatorDefinition convertStaticMethodToTranslatorDefinition(Class<?> clazz, Method method) {
        Class<? extends Translator> translatorClazz = translatorClazzMap.get(method.getParameterCount());
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
        definition.setParameterTypes(buildParamDesc(method));
        definition.setTranslatorClass(translatorClazz);
        definition.setMapperIndex(getIndexPair(method));
        definition.setTranslatorType(TranslatorType.STATIC_METHOD);
        definition.setTranslateDecorator(x -> x);
        definition.setInvokeBeanClazz(clazz);
        return definition;
    }


    @Override
    protected TranslatorDefinition convertDynamicMethodToTranslatorDefinition(Class<?> clazz, Method method, Translation translation) {
        Class<? extends Translator> translatorClazz = translatorClazzMap.get(method.getParameterCount());
        if (translatorClazz == null) {
            throw new TranslationException("Do not find the translator type with " + method.getParameterCount() + " params ,see https://kkkele.github.io/easy-translation/#/zh-cn/config/ for more information");
        }
        MethodHandle methodHandle;
        try {
            methodHandle = MethodConvert.getDynamicMethodHandle(translatorClazz, method);
        } catch (IllegalAccessException e) {
            throw new TranslationException("TranslatorDefinition produce failed",e);
        } catch (LambdaConversionException e) {
            throw new TranslationException("TranslatorDefinition produce failed",e);
        }
        BeanNameResolver beanNameResolver = singleton.computeIfAbsent(translation.beanNameResolver(), key -> {
            try {
                Constructor<? extends BeanNameResolver> constructor = key.getConstructor();
                return constructor.newInstance();
            } catch (NoSuchMethodException e) {
                throw new TranslationException("TranslatorDefinition produce failed",e);
            } catch (InvocationTargetException e) {
                throw new TranslationException("TranslatorDefinition produce failed",e);
            } catch (InstantiationException e) {
                throw new TranslationException("TranslatorDefinition produce failed",e);
            } catch (IllegalAccessException e) {
                throw new TranslationException("TranslatorDefinition produce failed",e);
            }
        });
        TranslatorDefinition definition = new TranslatorDefinition();
        definition.setReturnType(method.getReturnType());
        definition.setParameterTypes(buildParamDesc(method));
        definition.setTranslatorClass(translatorClazz);
        definition.setMapperIndex(getIndexPair(method));
        definition.setMethodHandle(methodHandle);
        definition.setTranslatorType(TranslatorType.DYNAMIC_METHOD);
        definition.setScope(translation.scope());
        definition.setTranslateDecorator(x -> x);
        definition.setInvokeBeanClazz(clazz);
        definition.setInvokeBeanName(beanNameResolver.resolve(translation.invokeBeanName()));
        return definition;
    }

    public int[] getIndexPair(Method method) {
        List<Integer> mapperIndexList = new LinkedList<>();
        List<Integer> otherIndexList = new LinkedList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(TransMapper.class)) {
                mapperIndexList.add(i);
            }else if (parameter.isAnnotationPresent(TransOther.class)){
                otherIndexList.add(i);
            }
        }
        //如果没有直接标注，则默认第一个参数为mapper,其他为other补充字段
        //如果有任何一个标记了@TransOther注解的方法参数，则上述规则不生效
        if (mapperIndexList.isEmpty() && parameters.length >= 1 && otherIndexList.isEmpty()) {
            return new int[1];
        }
        return mapperIndexList.stream().mapToInt(Integer::intValue).toArray();
    }

    public ParamDesc[] buildParamDesc(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        return Arrays.stream(genericParameterTypes)
                .map(type -> {
                    ParamDesc paramDesc = new ParamDesc();
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        paramDesc.setTargetClass(searchForClass(parameterizedType.getRawType().getTypeName()));
                        paramDesc.setTypes(Arrays.stream(parameterizedType.getActualTypeArguments())
                                .map(t -> searchForClass(t.getTypeName()))
                                .toArray(Class[]::new));
                    } else {
                        paramDesc.setTargetClass(searchForClass(type.getTypeName()));
                    }
                    return paramDesc;
                })
                .toArray(ParamDesc[]::new);
    }

    public Class<?> searchForClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }

}

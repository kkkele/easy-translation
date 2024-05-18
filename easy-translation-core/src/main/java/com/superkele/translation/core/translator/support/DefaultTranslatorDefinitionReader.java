package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.TransMapper;
import com.superkele.translation.annotation.TransValue;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.translator.MapperTranslator;
import com.superkele.translation.core.translator.Translator;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.ReflectUtils;

import java.lang.invoke.LambdaConversionException;
import java.lang.reflect.*;
import java.util.*;

public class DefaultTranslatorDefinitionReader extends AbstractTranslatorDefinitionReader {

    private Config config = new Config();

    public DefaultTranslatorDefinitionReader(String... locations) {
        super(locations);
    }

    public DefaultTranslatorDefinitionReader(TranslatorLoader translatorLoader, String[] locations) {
        super(translatorLoader, locations);
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
        translatorDefinition.setReturnType(mappedField.getType());
        translatorDefinition.setParameterTypes(new Class[]{declaredFields[mapperIndex].getType()});
        translatorDefinition.setTranslatorClass(MapperTranslator.class);
        translatorDefinition.setInvokeObj(null);
        Map<Object, Object> map = new HashMap<>();
        Field mapperField = declaredFields[mapperIndex];
        for (Enum enumConstant : enumClass.getEnumConstants()) {
            map.put(ReflectUtils.invokeGetter(enumConstant, mapperField.getName()), ReflectUtils.invokeGetter(enumConstant, mappedField.getName()));
        }
        MapperTranslator mapperTranslator = mapper -> map.get(mapper);
        translatorDefinition.setTranslator(mapperTranslator);
        translatorDefinition.setTranslateExecutor(null);
        translatorDefinition.setMapperIndex(new int[1]);
        return translatorDefinition;
    }

    @Override
    protected TranslatorDefinition convertStaticMethodToTranslatorDefinition(Method method) {
        TranslatorDefinition definition = new TranslatorDefinition();
        definition.setReturnType(method.getReturnType());
        definition.setParameterTypes(method.getParameterTypes());
        definition.setInvokeObj(null);
        Class<? extends Translator> translatorClazz = config.getTranslatorClazzMap().get(method.getParameterCount());
        definition.setTranslatorClass(translatorClazz);
        definition.setMapperIndex(getIndexPair(method));
        //关键，设置translator和translatorHandler
        try {
            Translator translator = MethodConvert.convertToFunctionInterface(translatorClazz, method);
            definition.setTranslator(translator);
            definition.setTranslateExecutor(args -> translator.doTranslate(args));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        }
        return definition;
    }

    @Override
    protected TranslatorDefinition convertDynamicMethodToTranslatorDefinition(Object invokeObj, Method method) {
        TranslatorDefinition definition = new TranslatorDefinition();
        definition.setReturnType(method.getReturnType());
        definition.setParameterTypes(method.getParameterTypes());
        definition.setInvokeObj(invokeObj);
        Class<? extends Translator> translatorClazz = config.getTranslatorClazzMap().get(method.getParameterCount());
        definition.setTranslatorClass(translatorClazz);
        definition.setMapperIndex(getIndexPair(method));
        //关键，设置translator和translatorHandler
        try {
            Translator translator = MethodConvert.convertToFunctionInterface(translatorClazz, invokeObj, method);
            definition.setTranslator(translator);
            definition.setTranslateExecutor(args -> translator.doTranslate(args));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        }
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

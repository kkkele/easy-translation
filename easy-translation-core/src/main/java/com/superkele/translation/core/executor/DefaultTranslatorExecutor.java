package com.superkele.translation.core.executor;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.context.TranslationFactory;
import com.superkele.translation.core.filter.TranslationFilterChain;
import com.superkele.translation.core.function.ConditionTranslator;
import com.superkele.translation.core.function.ContextTranslator;
import com.superkele.translation.core.function.MapperTranslator;
import com.superkele.translation.core.function.Translator;
import com.superkele.translation.core.metadata.FieldInfo;
import com.superkele.translation.core.util.ReflectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DefaultTranslatorExecutor implements TranslatorExecutor {

    private TranslationFactory factory;

    private Map<Class<?>, List<FieldInfo>> fieldInfoCache;

    private TranslationFilterChain filterChain;

    private Set<Class<?>> notMappingClazzCache;

    public DefaultTranslatorExecutor(TranslationFactory factory) {
        this.factory = factory;
    }

    @Override
    public Object execute(Object source) {
        if (source == null) {
            return null;
        }
        if (notMappingClazzCache == null) {
            synchronized (this) {
                if (notMappingClazzCache == null) {
                    notMappingClazzCache = new ConcurrentHashSet<>();
                }
            }
        }
        if (fieldInfoCache == null) {
            synchronized (this) {
                if (fieldInfoCache == null) {
                    fieldInfoCache = new ConcurrentHashMap<>();
                }
            }
        }
        if (factory == null) {
            throw new RuntimeException("context is null");
        }
        execute(source, factory);
        return source;
    }

    public Object execute(Object source, TranslationFactory context) {
        Class<?> clazz = source.getClass();
        if (notMappingClazzCache.contains(clazz)) {
            return source;
        }
        List<FieldInfo> fieldInfoList = fieldInfoCache.computeIfAbsent(clazz, declaringClass -> {
            Field[] declaredFields = clazz.getDeclaredFields();
            List<FieldInfo> collect = Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(Mapping.class))
                    .map(field -> {
                        Mapping mapping = field.getAnnotation(Mapping.class);
                        FieldInfo fieldInfo = new FieldInfo();
                        fieldInfo.setOriginField(field);
                        fieldInfo.setFieldName(field.getName());
                        fieldInfo.setTranslator(mapping.translator());
                        fieldInfo.setMapper(mapping.mapper());
                        fieldInfo.setOther(mapping.other());
                        fieldInfo.setReceive(mapping.receive());
                        fieldInfo.setTranslateTiming(mapping.timing());
                        fieldInfo.setNotNullMapping(mapping.notNullMapping());
                        return fieldInfo;
                    })
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(collect)) {
                notMappingClazzCache.add(clazz);
            }
            return collect;
        });
        if (CollectionUtil.isEmpty(fieldInfoList)) {
            return source;
        }
        for (FieldInfo fieldInfo : fieldInfoList) {
            String translatorName = fieldInfo.getTranslator();
            Translator translator = context.findTranslator(translatorName);
            String mapper = fieldInfo.getMapper();
            String other = fieldInfo.getOther();
            Object mappingValue = translate(source, mapper, other, translator);
            if (StringUtils.isNotBlank(fieldInfo.getReceive())) {
                mappingValue = ReflectUtils.invokeGetter(mappingValue, fieldInfo.getReceive());
            }
            ReflectUtils.invokeSetter(source, fieldInfo.getFieldName(), mappingValue);
        }
        return source;
    }

    @Override
    public Object execute(List<Object> sourceList) {
        return null;
    }

    @Override
    public void executeAsync(Object source) {

    }

    @Override
    public void executeAsync(List<Object> sourceList) {

    }

    public DefaultTranslatorExecutor setFilterChain(TranslationFilterChain filterChain) {
        this.filterChain = filterChain;
        return this;
    }

    public DefaultTranslatorExecutor setFactory(TranslationFactory factory) {
        this.factory = factory;
        return this;
    }


    private Object translate(Object source, String mapper, Object other, Translator translator) {
        Object translationValue = null;
        if (translator instanceof ConditionTranslator conditionTranslator) {
            translationValue = conditionTranslator.translate(ReflectUtils.invokeGetter(source, mapper), other);
        } else if (translator instanceof MapperTranslator mapsTranslator) {
            translationValue = mapsTranslator.translate(ReflectUtils.invokeGetter(source, mapper));
        } else if (translator instanceof ContextTranslator contextTranslator) {
            translationValue = contextTranslator.translate();
        }
        return translationValue;
    }
}

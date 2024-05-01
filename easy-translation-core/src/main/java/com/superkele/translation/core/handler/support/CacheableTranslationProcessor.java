package com.superkele.translation.core.handler.support;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.context.TransExecutorContext;
import com.superkele.translation.core.handler.TranslationProcessor;
import com.superkele.translation.core.metadata.FieldInfo;
import com.superkele.translation.core.translator.handle.TranslateExecutor;
import com.superkele.translation.core.util.ReflectUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class CacheableTranslationProcessor implements TranslationProcessor {

    public static int max_translator_param_len = 16;

    protected final Map<Class<?>, List<FieldInfo>> fieldInfoCache = new ConcurrentHashMap<>();

    protected final Set<Class<?>> notMappingClazzCache = new ConcurrentHashSet<>();

    protected abstract TransExecutorContext getContext();

/*    @Override
    public void process(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        if (notMappingClazzCache.contains(clazz)) {
            return;
        }
        fieldInfoCache.computeIfAbsent(clazz, declaringClass -> {
            Field[] declaredFields = clazz.getDeclaredFields();
            Map<Integer, List<FieldInfo>> sortListMap = Arrays.stream(declaredFields)
                    .filter(field -> field.isAnnotationPresent(Mapping.class))
                    .map(this::mapToFieldInfo)
                    .collect(Collectors.groupingBy(FieldInfo::getSort));
            if (CollectionUtil.isEmpty(sortListMap)) {
                notMappingClazzCache.add(clazz);
                return null;
            }
            List<List<FieldInfo>> collect = sortListMap.values()
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.get(0).getSort()))
                    .collect(Collectors.toList());

            return collect;
        });
        if (CollectionUtil.isEmpty(fieldInfoList)) {
            fieldInfoCache.remove(clazz);
            return;
        }
        processFields(obj, JobGroup);
    }



    protected abstract void processFields(Object obj, JobGroup jobGroup);


    public void translateValue(Object source, FieldInfo fieldInfo) {
        String translatorName = fieldInfo.getTranslator();
        TranslateExecutor executor = getContext().findExecutor(translatorName);
        //将 mapper字段和 other字段调整位置
        int mapperLength = fieldInfo.getMapper().length;
        int otherLength = fieldInfo.getOther().length;
        Object[] args = new Object[max_translator_param_len];
        for (int i = 0; i < mapperLength; i++) {
            if (StringUtils.isNotBlank(fieldInfo.getMapper()[i])) {
                args[i] = ReflectUtils.invokeGetter(source, fieldInfo.getMapper()[i]);
            }
        }
        int j = 0;
        for (int i = mapperLength; i < mapperLength + otherLength; i++) {
            args[i] = fieldInfo.getOther()[j++];
        }
        Object mappingValue = executor.execute(args);
        ReflectUtils.invokeSetter(source, fieldInfo.getFieldName(), mappingValue);
    }

    protected FieldInfo mapToFieldInfo(Field field) {
        Mapping mapping = field.getAnnotation(Mapping.class);
        FieldInfo fieldInfo = new FieldInfo();
        BeanUtil.copyProperties(mapping, fieldInfo);
        fieldInfo.setOriginField(field);
        return fieldInfo;
    }*/

}

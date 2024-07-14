package com.superkele.translation.core.metadata.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.core.metadata.FieldTranslationInfo;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationInfoFactory;
import com.superkele.translation.core.metadata.FieldTranslationRegistry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractFieldTranslationInfoFactory implements FieldTranslationInfoFactory, FieldTranslationRegistry {

    private final Map<Class<?>, FieldTranslationInfo> commonMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, FieldTranslationInfo> jsonMap = new ConcurrentHashMap<>();
    private final Set<Class<?>> notExists = new ConcurrentHashSet<>();

    protected abstract FieldTranslationBuilder getBuilder();

    @Override
    public FieldTranslationInfo get(Class<?> clazz, boolean isJsonSerialize) {
        if (isJsonSerialize) {
            FieldTranslationInfo fieldTranslationInfo = jsonMap.get(clazz);
            if (fieldTranslationInfo != null) {
                return fieldTranslationInfo;
            }
            fieldTranslationInfo = getBuilder().build(clazz, true);
            if (fieldTranslationInfo != null) {
                jsonMap.put(clazz, fieldTranslationInfo);
            }
            return fieldTranslationInfo;
        } else {
            if (notExists.contains(clazz)) {
                return null;
            }
            FieldTranslationInfo fieldTranslationInfo = commonMap.get(clazz);
            if (fieldTranslationInfo != null) {
                return fieldTranslationInfo;
            }
            fieldTranslationInfo = getBuilder().build(clazz, false);
            if (fieldTranslationInfo != null) {
                commonMap.put(clazz, fieldTranslationInfo);
            } else {
                notExists.add(clazz);
            }
            return fieldTranslationInfo;
        }
    }


    @Override
    public void register(Class<?> clazz, boolean isJsonSerialize, FieldTranslationInfo fieldTranslationInfo) {
        if (isJsonSerialize) {
            jsonMap.put(clazz, fieldTranslationInfo);
        } else {
            commonMap.put(clazz, fieldTranslationInfo);
        }
    }

}

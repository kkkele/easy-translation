package com.superkele.translation.core.metadata.support;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationFactory;
import com.superkele.translation.core.metadata.FieldTranslationRegistry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractFieldTranslationFactory implements FieldTranslationFactory, FieldTranslationRegistry {

    private final Map<Class<?>, FieldTranslation> commonMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, FieldTranslation> jsonMap = new ConcurrentHashMap<>();

    protected abstract FieldTranslationBuilder getBuilder();

    private final Set<Class<?>> notExists = new ConcurrentHashSet<>();

    @Override
    public FieldTranslation get(Class<?> clazz, boolean isJsonSerialize) {
        if (isJsonSerialize) {
            FieldTranslation fieldTranslation = jsonMap.get(clazz);
            if (fieldTranslation != null) {
                return fieldTranslation;
            }
            fieldTranslation = getBuilder().build(clazz, true);
            if (fieldTranslation != null) {
                jsonMap.put(clazz, fieldTranslation);
            }
            return fieldTranslation;
        } else {
            if (notExists.contains(clazz)){
                return null;
            }
            FieldTranslation fieldTranslation = commonMap.get(clazz);
            if (fieldTranslation != null) {
                return fieldTranslation;
            }
            fieldTranslation = getBuilder().build(clazz, false);
            if (fieldTranslation != null) {
                commonMap.put(clazz, fieldTranslation);
            }else{
                notExists.add(clazz);
            }
            return fieldTranslation;
        }
    }


    @Override
    public void register(Class<?> clazz, boolean isJsonSerialize, FieldTranslation fieldTranslation) {
        if (isJsonSerialize) {
            jsonMap.put(clazz, fieldTranslation);
        } else {
            commonMap.put(clazz, fieldTranslation);
        }
    }

}

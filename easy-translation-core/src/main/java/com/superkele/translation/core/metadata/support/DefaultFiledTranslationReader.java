package com.superkele.translation.core.metadata.support;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationReader;
import com.superkele.translation.core.metadata.FieldTranslationRegistry;
import com.superkele.translation.core.util.ReflectionsPlus;
import com.superkele.translation.core.util.ScannerEnum;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class DefaultFiledTranslationReader implements FieldTranslationReader {

    protected final FieldTranslationRegistry registry;

    protected final FieldTranslationBuilder builder;

    public DefaultFiledTranslationReader(FieldTranslationRegistry registry, FieldTranslationBuilder builder) {
        this.registry = registry;
        this.builder = builder;
    }

    @Override
    public FieldTranslationRegistry getRegister() {
        return registry;
    }

    @Override
    public void load(String[] basePath) {
        ReflectionsPlus reflectionsPlus = ReflectionsPlus.getReflectionsPlus(ListUtil.of(ScannerEnum.FILED),basePath);
        Set<Field> fieldsMergedAnnotatedWith = reflectionsPlus.getFieldsMergedAnnotatedWith(Mapping.class);
        Set<Class<?>> clazzSet = new HashSet<>();
        fieldsMergedAnnotatedWith.forEach(field -> {
            Class<?> declaringClass = field.getDeclaringClass();
            if (clazzSet.contains(declaringClass)){
                return;
            }
            clazzSet.add(declaringClass);
            FieldTranslation common = builder.build(declaringClass, false);
            if (common != null){
                registry.register(declaringClass, false, common);
            }
            FieldTranslation json = builder.build(declaringClass, true);
            if (json != null){
                registry.register(declaringClass, true, json);
            }
        });
    }
}

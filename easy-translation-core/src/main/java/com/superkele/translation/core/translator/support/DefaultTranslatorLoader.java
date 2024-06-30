package com.superkele.translation.core.translator.support;

import cn.hutool.core.collection.ListUtil;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
import com.superkele.translation.core.util.ReflectionsPlus;
import com.superkele.translation.core.util.ScannerEnum;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.Utils;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultTranslatorLoader implements TranslatorLoader {


    @Override
    public Map<Class<?>, Set<Method>> getTranslator(String location) {
        ReflectionsPlus reflections = ReflectionsPlus.getReflectionsPlus(ListUtil.of(ScannerEnum.METHOD,ScannerEnum.TYPE),location);
        //装载枚举
        Set<Class<?>> enumClazzSet = reflections.getTypesMergedAnnotatedWith(Translation.class,false);
        //装载方法
        Set<Method> methodsAnnotatedWith = reflections.getMethodsMergedAnnotatedWith(Translation.class);
        Map<Class<?>, Set<Method>> res = new HashMap<>();
        enumClazzSet.stream()
                .filter(clazz -> clazz.isEnum())
                .forEach(clazz -> res.put(clazz, null));
        Map<? extends Class<?>, List<Method>> groupBy = methodsAnnotatedWith.stream()
                .collect(Collectors.groupingBy(Method::getDeclaringClass));
        //如果enum存在其他Translator方法，则 key,null 会被 k,v覆盖
        groupBy.forEach((k, v) -> res.put(k, new HashSet<>(v)));
        return res;
    }


}

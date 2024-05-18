package com.superkele.translation.core.translator.support;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class DefaultTranslatorLoader implements TranslatorLoader {

    private final SubTypesScanner subTypesScanner = new SubTypesScanner();
    private final TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
    private final MethodAnnotationsScanner methodAnnotationsScanner = new MethodAnnotationsScanner();

    @Override
    public Map<Class<?>, Set<Method>> getTranslator(String location) {
        Reflections reflections = new Reflections(location, typeAnnotationsScanner, subTypesScanner, methodAnnotationsScanner);
        //装载枚举
        Set<Class<?>> enumClazzSet = reflections.getTypesAnnotatedWith(Translation.class);
        //装载方法
        Set<Method> methodsAnnotatedWith = reflections.getMethodsAnnotatedWith(Translation.class);
        Map<Class<?>, Set<Method>> res = new HashMap<>();
        for (Class<?> clazz : enumClazzSet) {
            res.put(clazz, null);
        }
        Map<? extends Class<?>, List<Method>> groupBy = methodsAnnotatedWith.stream()
                .collect(Collectors.groupingBy(Method::getDeclaringClass));
        groupBy.forEach((k, v) -> res.put(k, new HashSet<>(v)));
        return res;
    }
}

package com.superkele.translation.core.translator.support;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.core.translator.definition.TranslatorLoader;
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
        ReflectionsPlus reflections = getReflectionsPlus(location);
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
        groupBy.forEach((k, v) -> res.put(k, new HashSet<>(v)));
        return res;
    }

    private ReflectionsPlus getReflectionsPlus(String location) {
        MethodMergedAnnotationsScanner methodAnnotationsScanner = new MethodMergedAnnotationsScanner();
        SubTypesScanner subTypesScanner = new SubTypesScanner();
        TypeMergedAnnotationsScanner typeMergedAnnotationsScanner = new TypeMergedAnnotationsScanner();
        TypeAnnotationsScanner typeAnnotationsScanner = new TypeAnnotationsScanner();
        ReflectionsPlus reflections = new ReflectionsPlus(location, typeAnnotationsScanner, subTypesScanner, methodAnnotationsScanner,typeMergedAnnotationsScanner);
        return reflections;
    }

    public static class ReflectionsPlus extends Reflections {
        public ReflectionsPlus(String prefix, Scanner... scanners) {
            super(prefix, scanners);
        }

        private static String index(Class<? extends Scanner> scannerClass) {
            return scannerClass.getSimpleName();
        }

        public Set<Method> getMethodsMergedAnnotatedWith(Class<? extends Annotation> annotation) {
            Iterable<String> methods = this.store.get(index(MethodMergedAnnotationsScanner.class), new String[]{annotation.getName()});
            return Utils.getMethodsFromDescriptors(methods, this.configuration.getClassLoaders());
        }


        public Set<Class<?>> getTypesMergedAnnotatedWith(Class<? extends Annotation> annotation, boolean honorInherited) {
            Iterable<String> annotated = this.store.get(index(TypeMergedAnnotationsScanner.class), new String[]{annotation.getName()});
            Iterable<String> classes = this.getAllAnnotated(annotated, annotation.isAnnotationPresent(Inherited.class), honorInherited);
            return Sets.newHashSet(Iterables.concat(ReflectionUtils.forNames(annotated, this.configuration.getClassLoaders()), ReflectionUtils.forNames(classes, this.configuration.getClassLoaders())));
        }


    }

    public  class MethodMergedAnnotationsScanner extends MethodAnnotationsScanner {
        @Override
        public void scan(Object cls) {
            Iterator var2 = this.getMetadataAdapter().getMethods(cls).iterator();

            while (var2.hasNext()) {
                Object method = var2.next();
                Iterator var4 = this.getMetadataAdapter().getMethodAnnotationNames(method).iterator();
                while (var4.hasNext()) {
                    String methodAnnotation = (String) var4.next();
                    String methodFullKey = this.getMetadataAdapter().getMethodFullKey(cls, method);
                    if (this.acceptResult(methodAnnotation)) {
                        this.getStore().put(methodAnnotation, methodFullKey);
                    }
                    try {
                        Class<?> annotationClazz = Class.forName(methodAnnotation);
                        ArrayList<String> objects = new ArrayList<>();
                        findAnnotation(annotationClazz, objects);
                        String[] array = objects.stream().toArray(String[]::new);
                        for (String s : array) {
                            if (this.acceptResult(s)) {
                                this.getStore().put(s, methodFullKey);
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public class TypeMergedAnnotationsScanner extends TypeAnnotationsScanner{
        @Override
        public void scan(Object cls) {
            String className = this.getMetadataAdapter().getClassName(cls);
            Iterator var3 = this.getMetadataAdapter().getClassAnnotationNames(cls).iterator();

            while(true) {
                String annotationType;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    annotationType = (String)var3.next();
                } while(!this.acceptResult(annotationType) && !annotationType.equals(Inherited.class.getName()));
                this.getStore().put(annotationType, className);
                try {
                    Class<?> annotationClazz = Class.forName(annotationType);
                    ArrayList<String> objects = new ArrayList<>();
                    findAnnotation(annotationClazz, objects);
                    String[] array = objects.stream().toArray(String[]::new);
                    for (String s : array) {
                        if (this.acceptResult(s)) {
                            this.getStore().put(s, className);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void findAnnotation(Class<?> annotationClazz, List<String> res) {
        Annotation[] annotations = annotationClazz.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> clazz = annotation.annotationType();
            if (clazz != Deprecated.class &&
                    clazz != SuppressWarnings.class &&
                    clazz != Override.class &&
                    clazz != Target.class &&
                    clazz != Retention.class &&
                    clazz != Documented.class &&
                    clazz != Inherited.class) {
                findAnnotation(clazz, res);
                res.add(clazz.getName());
            }
        }
    }
}

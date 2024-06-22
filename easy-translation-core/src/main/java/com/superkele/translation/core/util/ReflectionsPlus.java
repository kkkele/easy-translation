package com.superkele.translation.core.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.reflections.Configuration;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.*;
import org.reflections.scanners.Scanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.Utils;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionsPlus extends Reflections {
    public ReflectionsPlus(String prefix, Scanner... scanners) {
        super(prefix, scanners);
    }

    public ReflectionsPlus(Configuration configuration) {
        super(configuration);
    }

    public static ReflectionsPlus getReflectionsPlus(String... locations) {
        ReflectionsPlus reflections = new ReflectionsPlus(new ConfigurationBuilder()
                .setUrls(Arrays.stream(locations)
                        .map(ClasspathHelper::forPackage)
                        .flatMap(Collection::stream)
                        .distinct()
                        .collect(Collectors.toList()))
                .setScanners(new MethodMergedAnnotationsScanner(),
                        new FieldMergedAnnotationsScanner(),
                        new SubTypesScanner(),
                        new TypeMergedAnnotationsScanner(),
                        new TypeAnnotationsScanner())
        );
        return reflections;
    }

    private static String index(Class<? extends Scanner> scannerClass) {
        return scannerClass.getSimpleName();
    }

    public static void findAnnotation(Class<?> annotationClazz, List<String> res) {
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

    public Set<Method> getMethodsMergedAnnotatedWith(Class<? extends Annotation> annotation) {
        Iterable<String> methods = this.store.get(index(MethodMergedAnnotationsScanner.class), new String[]{annotation.getName()});
        return Utils.getMethodsFromDescriptors(methods, this.configuration.getClassLoaders());
    }

    public Set<Class<?>> getTypesMergedAnnotatedWith(Class<? extends Annotation> annotation, boolean honorInherited) {
        Iterable<String> annotated = this.store.get(index(TypeMergedAnnotationsScanner.class), new String[]{annotation.getName()});
        Iterable<String> classes = this.getAllAnnotated(annotated, annotation.isAnnotationPresent(Inherited.class), honorInherited);
        return Sets.newHashSet(Iterables.concat(ReflectionUtils.forNames(annotated, this.configuration.getClassLoaders()), ReflectionUtils.forNames(classes, this.configuration.getClassLoaders())));
    }

    public Set<Field> getFieldsMergedAnnotatedWith(Class<? extends Annotation> annotation) {
        Set<Field> result = Sets.newHashSet();
        Iterator var3 = this.store.get(index(FieldMergedAnnotationsScanner.class), new String[]{annotation.getName()}).iterator();
        while (var3.hasNext()) {
            String annotated = (String) var3.next();
            result.add(Utils.getFieldFromString(annotated, this.configuration.getClassLoaders()));
        }
        return result;
    }


    public static class MethodMergedAnnotationsScanner extends MethodAnnotationsScanner {
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

    public static class FieldMergedAnnotationsScanner extends FieldAnnotationsScanner {
        @Override
        public void scan(Object cls) {
            String className = this.getMetadataAdapter().getClassName(cls);
            List<Object> fields = this.getMetadataAdapter().getFields(cls);
            Iterator var4 = fields.iterator();

            while (var4.hasNext()) {
                Object field = var4.next();
                List<String> fieldAnnotations = this.getMetadataAdapter().getFieldAnnotationNames(field);
                Iterator var7 = fieldAnnotations.iterator();

                while (var7.hasNext()) {
                    String fieldAnnotation = (String) var7.next();
                    if (this.acceptResult(fieldAnnotation)) {
                        String fieldName = this.getMetadataAdapter().getFieldName(field);
                        this.getStore().put(fieldAnnotation, String.format("%s.%s", className, fieldName));
                    } else {
                        try {
                            Class<?> annotationClazz = Class.forName(fieldAnnotation);
                            ArrayList<String> objects = new ArrayList<>();
                            findAnnotation(annotationClazz, objects);
                            String[] array = objects.stream().toArray(String[]::new);
                            for (String s : array) {
                                if (this.acceptResult(s)) {
                                    String fieldName = this.getMetadataAdapter().getFieldName(field);
                                    this.getStore().put(s, String.format("%s.%s", className, fieldName));
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public static class TypeMergedAnnotationsScanner extends TypeAnnotationsScanner {
        @Override
        public void scan(Object cls) {
            String className = this.getMetadataAdapter().getClassName(cls);
            Iterator var3 = this.getMetadataAdapter().getClassAnnotationNames(cls).iterator();

            while (true) {
                String annotationType;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    annotationType = (String) var3.next();
                } while (!this.acceptResult(annotationType) && !annotationType.equals(Inherited.class.getName()));
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
}

package com.czy.test;

import cn.hutool.core.annotation.AnnotationUtil;
import com.superkele.idempotent.annotations.Idempotent;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.boot.annotation.Translator;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.Scanner;
import org.reflections.util.Utils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;

import javax.annotation.Generated;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnnotatedUtil {

    @Test
    public void test() {
        Method method = null;
        try {
            method = Person.class.getDeclaredMethod("method");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        MethodMergedAnnotationsScanner methodAnnotationsScanner = new MethodMergedAnnotationsScanner();
        ReflectionsPlus reflections = new ReflectionsPlus("com.czy.test", methodAnnotationsScanner);
        Set<Method> methodsAnnotatedWith = reflections.getMethodsMergedAnnotatedWith(Translation.class);
        System.out.println(methodsAnnotatedWith);
        Translation translation0 = AnnotationUtil.getAnnotation(method, Translation.class);
        System.out.println(translation0);
        Translation translation = AnnotatedElementUtils.getMergedAnnotation(method, Translation.class);
        System.out.println(translation);
    }

    public static class ReflectionsPlus extends Reflections {
        public ReflectionsPlus(String s, MethodAnnotationsScanner methodAnnotationsScanner) {
            super(s, methodAnnotationsScanner);
        }

        private static String index(Class<? extends Scanner> scannerClass) {
            return scannerClass.getSimpleName();
        }

        public Set<Method> getMethodsMergedAnnotatedWith(Class<? extends Annotation> annotation) {
            Iterable<String> methods = this.store.get(index(MethodMergedAnnotationsScanner.class), new String[]{annotation.getName()});
            return Utils.getMethodsFromDescriptors(methods, this.configuration.getClassLoaders());
        }
    }

    class Person {

        @Translator("Hello World")
        @Cacheable
        @Idempotent
        public void method() {

        }

        @Translator("Hello World")
        @Idempotent
        public void methodA() {

        }

    }

    class MethodMergedAnnotationsScanner extends MethodAnnotationsScanner {
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
                        findAnnotation(annotationClazz,objects);
                        String[] array = objects.stream().toArray(String[]::new);
                        for (String s : array){
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
}

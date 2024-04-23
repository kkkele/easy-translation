package com.superkele.translation.core.scaner;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public class DefaultAnnotationScanner implements AnnotationScanner {
    @Override
    public Set<Field> getAnnotatedFields(String packageName, Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(packageName, new FieldAnnotationsScanner());
        return reflections.getFieldsAnnotatedWith(annotationClass);
    }

    @Override
    public Set<Method> getAnnotatedMethod(String packageName, Class<? extends Annotation> annotationClass) {
        Reflections reflections = new Reflections(packageName, new MethodAnnotationsScanner());
        return reflections.getMethodsAnnotatedWith(annotationClass);
    }
}

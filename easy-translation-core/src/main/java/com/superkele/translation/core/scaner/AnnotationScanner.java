package com.superkele.translation.core.scaner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface AnnotationScanner {

    Set<Field> getAnnotatedFields(String packageName, Class<? extends Annotation> annotationClass);
    Set<Method> getAnnotatedMethod(String packageName, Class<? extends Annotation> annotationClass);
}

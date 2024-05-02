package com.superkele.translation.test.annotation;

import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.springframework.core.annotation.AnnotatedElementUtils;


import java.lang.annotation.*;
import java.util.Set;

import static org.springframework.core.annotation.AnnotatedElementUtils.getMergedAnnotation;

public class AnnotationTest {

    @Test
    public void test(){
        Component mergedAnnotation = getMergedAnnotation(Student.class , Component.class);
        System.out.println(mergedAnnotation);
    }


    @Inherited
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Component{
        String value() default "";
    }

    @Inherited
    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Service{
        String value() default "";
    }

    @Service("小明")
    static class Student{

    }
}

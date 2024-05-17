package com.superkele.translation.test.lambdametafactory;

import com.superkele.translation.core.convert.MethodConvert;
import com.superkele.translation.core.property.support.DefaultMethodHandlePropertyHandler;
import com.superkele.translation.core.translator.ContextTranslator;
import com.superkele.translation.core.util.ReflectUtils;
import com.superkele.translation.test.util.TimeRecorder;
import lombok.Data;
import org.junit.Test;

import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * 性能优化测试
 */
public class PerformanceTest {

    Random random = new Random();

    ContextTranslator lambdaMethod;
    Method method;

    Method getPerson;

    Method getName;

    MethodHandle getPersonMethodHandle;

    MethodHandle getNameMethodHandle;

    DefaultMethodHandlePropertyHandler defaultMethodHandlePropertyHandler = new DefaultMethodHandlePropertyHandler();


    {
        try {
            lambdaMethod = MethodConvert.convertToFunctionInterface(ContextTranslator.class, this, PerformanceTest.class.getMethod("method1"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    {
        try {
            method = PerformanceTest.class.getMethod("method1");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            getPerson = Person.class.getMethod("getPerson");
            getName = Person.class.getMethod("getName");
            getPerson.setAccessible(true);
            getName.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            getPersonMethodHandle = MethodConvert.getMethodHandle(PropertyGetter.class, Person.class.getMethod("getPerson"));
            getNameMethodHandle = MethodConvert.getMethodHandle(PropertyGetter.class, Person.class.getMethod("getName"));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (LambdaConversionException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 0
     * 712ms
     * 714ms
     * 1
     * 711ms
     * 695ms
     * 2
     * 715ms
     * 686ms
     * 3
     * 728ms
     * 710ms
     * 4
     * 729ms
     * 693ms
     * 证明lambdaMetafactory性能不差
     */
    @Test
    public void test() throws NoSuchMethodException, LambdaConversionException, IllegalAccessException {
        for (int i = 0; i < 10000000; i++) {
            method1();
        }
        for (int i = 0; i < 10000000; i++) {
            method2();
        }
        long record = TimeRecorder.record(this::method1, 100000000);
        System.out.println(record + "ms");
        record = TimeRecorder.record(this::method2, 100000000);
        System.out.println(record + "ms");
    }

    /**
     * 0
     * 712ms
     * 691ms
     * 1
     * 706ms
     * 692ms
     * 2
     * 732ms
     * 724ms
     * <p>
     * 3
     * 718ms
     * 714ms
     * 4
     * 719ms
     * 704ms
     */
    @Test
    public void test2() {
        for (int i = 0; i < 10000000; i++) {
            method1();
        }
        for (int i = 0; i < 10000000; i++) {
            method3();
        }
        long record = TimeRecorder.record(this::method1, 100000000);
        System.out.println(record + "ms");
        record = TimeRecorder.record(this::method3, 100000000);
        System.out.println(record + "ms");
    }

    @Test
    public void test3() throws Exception {
        Person person = new Person();
        person.setName("superkele");
        Person son = new Person();
        son.setName("son");
        person.setPerson(son);
        for (int i = 0; i < 10000000; i++) {
            person.getPerson().getName();
            Person p = (Person) getPerson.invoke(person);
            getName.invoke(p);
            ReflectUtils.invokeGetter(person, "person.name");
            defaultMethodHandlePropertyHandler.invokeGetter(person, "person.name");
        }

        long l1 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            person.getPerson().getName();
        }
        long l2 = System.currentTimeMillis();
        System.out.println("直接调用 cost : " + (l2 - l1) + "ms");

        long begin = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            getName.invoke(getPerson.invoke(person));
        }
        long end = System.currentTimeMillis();
        System.out.println("反射调用 cost : " + (end - begin) + "ms");

        long begin1 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            ReflectUtils.invokeGetter(person, "person.name");
        }
        long end1 = System.currentTimeMillis();
        System.out.println("使用工具类 cost : " + (end1 - begin1) + "ms");

        long r1 = System.currentTimeMillis();
        for (int i = 0; i < 10000000; i++) {
            defaultMethodHandlePropertyHandler.invokeGetter(person, "person.name");
        }
        long r2 = System.currentTimeMillis();
        System.out.println("czy优化后 cost : " + (r2 - r1) + "ms");
    }

    public int method1() {
        int a = random.nextInt();
        int b = random.nextInt();
        return a + b;
    }

    public int method2() {
        return (int) lambdaMethod.translate();
    }

    public int method3() {
        try {
            return (int) method.invoke(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    @Data
    public static class Person {
        private String name;
        private Person person;
    }

}

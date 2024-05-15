package com.superkele.translation.core.util;

import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author Czy
 */
@SuppressWarnings("rawtypes")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MethodUtils extends ReflectUtil {

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final Map<Class<?>, Pair<Method, MethodType>> FUNCTION_INTERFACE_CACHE = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Map<String, Method>> METHOD_CACHE = new HashMap<>();


    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        String[] methods = StringUtils.split(propertyName, ".");
        for (String name : methods) {
            String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
            Class<?> objClazz = object.getClass();
            Map<String, Method> methodMap = METHOD_CACHE.computeIfAbsent(objClazz, k -> new HashMap<>());
            Method method = methodMap.computeIfAbsent(getterMethodName, methodName -> {
                try {
                    return objClazz.getMethod(methodName);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                object = method.invoke(object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return (E) object;
    }



    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E value) {
        if (StringUtils.isBlank(propertyName)) {
            return;
        }
        Object object = obj;
        int index = StringUtils.lastIndexOf(propertyName, ".");
        if (index != -1) {
            String getterProperty = StringUtils.substring(propertyName, 0, index);
            object = invokeGetter(object, getterProperty);
        }
        String setterProperty = StringUtils.substring(propertyName, index + 1);
        String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(setterProperty);
        Class<?> objClazz = object.getClass();
        Map<String, Method> methodMap = METHOD_CACHE.computeIfAbsent(objClazz, k -> new ConcurrentHashMap<>());
        Method method = methodMap.computeIfAbsent(setterMethodName, methodName -> {
            try {
                return objClazz.getMethod(methodName, objClazz.getDeclaredField(setterProperty).getType());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        });
        try {
            method.invoke(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isStaticMethod(Method method) {
        Assert.notNull(method, "method can not be null");
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isAbstractMethod(Method method) {
        Assert.notNull(method, "method can not be null");
        return Modifier.isAbstract(method.getModifiers());
    }


    /**
     * 获取函数式接口的lambda方法的MethodType
     *
     * @param functionInterface 函数式接口
     * @return
     */
    public static Pair<Method, MethodType> findFunctionInterfaceMethodType(Class<?> functionInterface) {
        return FUNCTION_INTERFACE_CACHE.computeIfAbsent(functionInterface, key -> {
            Assert.notNull(functionInterface, "target function interface can not be null");
            Method[] methods = functionInterface.getMethods();
            if (functionInterface.isAnnotationPresent(FunctionalInterface.class)) {
                for (Method method : methods) {
                    if (Modifier.isAbstract(method.getModifiers())) {
                        return Pair.of(method, MethodType.methodType(method.getReturnType(), method.getParameterTypes()));
                    }
                }
            } else {
                Method resultMethod = null;
                int count = 0;
                for (Method method : methods) {
                    if (Modifier.isAbstract(method.getModifiers())) {
                        resultMethod = method;
                        count++;
                    }
                }
                if (count == 1) {
                    return Pair.of(resultMethod, MethodType.methodType(resultMethod.getReturnType(), resultMethod.getParameterTypes()));
                }
            }
            throw new IllegalStateException(functionInterface.getName() + "is not a function interface");
        });
    }

}

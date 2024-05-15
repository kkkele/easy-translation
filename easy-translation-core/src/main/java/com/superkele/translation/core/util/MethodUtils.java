package com.superkele.translation.core.util;

import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    private static final Map<Pair<Class<?>, String>, PropertyGetter> PROPERTY_GETTER_CACHE = new ConcurrentHashMap<>();

    private static final Map<Pair<Class<?>, String>, PropertySetter> PROPERTY_SETTER_CACHE = new ConcurrentHashMap<>();

    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object object = obj;
        PropertyGetter propertyGetter = PROPERTY_GETTER_CACHE.computeIfAbsent(Pair.of(obj.getClass(), propertyName), pair -> {
            Class<?> key = pair.getKey();
            String value = pair.getValue();
            String[] properties = StringUtils.split(value, ".");
            Method[] getterMethods = new Method[properties.length];
            for (int i = 0; i < getterMethods.length; i++) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(properties[i]);
                try {
                    Method method = key.getMethod(getterMethodName);
                    key = method.getReturnType();
                    getterMethods[i] = method;
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            return new PropertyGetter(getterMethods);
        });
        return (E) propertyGetter.get(object);
    }


    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E setterValue) {
        if (StringUtils.isBlank(propertyName)) {
            return;
        }
        PROPERTY_SETTER_CACHE.computeIfAbsent(Pair.of(obj.getClass(), propertyName), pair -> {
            Class<?> key = pair.getKey();
            String pName = pair.getValue();
            String[] properties = StringUtils.split(pName, ".");
            Method[] getterMethods = new Method[properties.length - 1];
            for (int i = 0; i < getterMethods.length; i++) {
                String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(properties[i]);
                try {
                    Method method = key.getMethod(getterMethodName);
                    key = method.getReturnType();
                    getterMethods[i] = method;
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                String setterName = SETTER_PREFIX + StringUtils.capitalize(properties[properties.length - 1]);
                Method setterMethod = key.getMethod(setterName, key.getDeclaredField(properties[properties.length - 1]).getType());
                return new PropertySetter(getterMethods, setterMethod);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }).set(obj,setterValue);
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

    static class PropertyGetter {
        private final Method[] getterMethods;

        PropertyGetter(Method[] getterMethods) {
            this.getterMethods = getterMethods;
        }

        public Object get(Object object) {
            Object res = object;
            for (int i = 0; i < getterMethods.length; i++) {
                try {
                    res = getterMethods[i].invoke(res);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            return res;
        }
    }

    static class PropertySetter {
        private final Method[] getterMethods;

        private final Method setterMethod;

        PropertySetter(Method[] getterMethods, Method setterMethods) {
            this.getterMethods = getterMethods;
            this.setterMethod = setterMethods;
        }

        public void set(Object object, Object value) {
            Object res = object;
            for (int i = 0; i < getterMethods.length; i++) {
                try {
                    res = getterMethods[i].invoke(object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                setterMethod.invoke(res, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

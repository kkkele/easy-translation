package com.superkele.translation.core.util;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ReflectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;


/**
 * 反射工具类. 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 *
 * @author Czy
 */
@SuppressWarnings("rawtypes")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectUtils extends ReflectUtil {


    private static final Map<Class<?>, Pair<Method, MethodType>> FUNCTION_INTERFACE_CACHE = new WeakConcurrentMap<>();



    /**
     * 调用Getter方法.
     * 支持多级，如：对象名.对象名.方法
     */
    @SuppressWarnings("unchecked")
    public static <E> E invokeGetter(Object obj, String propertyName) {
        Object res = obj;
        String[] properties = StringUtils.split(propertyName, ".");
        for (String property : properties) {
            res = getFieldValue(res, property);
        }
        return (E) res;
    }


    /**
     * 调用Setter方法, 仅匹配方法名。
     * 支持多级，如：对象名.对象名.方法
     */
    public static <E> void invokeSetter(Object obj, String propertyName, E setterValue) {
        Object res = obj;
        String[] properties = StringUtils.split(propertyName, ".");
        for (int i = 0; i < properties.length - 1; i++) {
            res = getFieldValue(res, properties[i]);
        }
        setFieldValue(res, properties[properties.length - 1], setterValue);
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

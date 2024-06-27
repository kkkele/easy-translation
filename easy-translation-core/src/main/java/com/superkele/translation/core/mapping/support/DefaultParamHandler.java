package com.superkele.translation.core.mapping.support;

import cn.hutool.core.convert.Convert;
import com.superkele.translation.core.exception.ParamHandlerException;
import com.superkele.translation.core.mapping.ParamHandler;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ParamHandler默认实现
 */
public class DefaultParamHandler implements ParamHandler<Object, Object> {

    @Override
    public Object wrapper(Object param, Class<Object> sourceClazz, Class<Object> targetClazz, Class[] types) throws ParamHandlerException {
        if (sourceClazz.isAssignableFrom(targetClazz)) {
            return param;
        }
        if (targetClazz.isArray()) {
            Class<?> componentType = targetClazz.getComponentType();
            Object array = Array.newInstance(componentType, 1);
            if (param == null || sourceClazz.isAssignableFrom(componentType)) {
                Array.set(array, 0, param);
            } else {
                Array.set(array, 0, Convert.convert(componentType, param));
            }
            return array;
        } else if (ArrayList.class.isAssignableFrom(targetClazz)) {
            ArrayList arrayList = new ArrayList(1);
            param = predictAndProcess(param, types);
            arrayList.add(param);
            return arrayList;
        } else if (HashSet.class.isAssignableFrom(targetClazz)) {
            HashSet<Object> set = new HashSet<>();
            param = predictAndProcess(param, types);
            set.add(param);
            return set;
        } else if (LinkedList.class.isAssignableFrom(targetClazz)) {
            LinkedList<Object> linkedList = new LinkedList<>();
            param = predictAndProcess(param, types);
            linkedList.add(param);
            return linkedList;
        } else {
            return Convert.convert(targetClazz, param);
        }
    }

    /**
     * 判断泛型类型是否匹配并转换
     *
     * @param param
     * @param types
     * @return
     */
    private Object predictAndProcess(Object param, Class[] types) {
        if (types != null && types.length > 0 && param != null) {
            if (param != null && !types[0].isInstance(param)) {
                param = Convert.convert(types[0], param);
            }
        }
        return param;
    }

    @Override
    public Object wrapperBatch(List<Object> params, Class<Object> sourceClazz, Class<Object> targetClazz, Class[] types) throws ParamHandlerException {
        //判断泛型是否相同
        boolean isSameType = true;
        if (types != null && types.length > 0) {
            if (!sourceClazz.isAssignableFrom(types[0])) {
                isSameType = false;
            }
        }
        if (targetClazz.isInstance(params)) {
            if (isSameType) {
                return params;
            } else {
                return params.stream()
                        .map(param -> Convert.convert(types[0], param))
                        .collect(Collectors.toList());
            }
        }
        if (targetClazz.isArray()) {
            Class<?> componentType = targetClazz.getComponentType();
            Object array = Array.newInstance(componentType, params.size());
            if (sourceClazz.isAssignableFrom(componentType)) {
                for (int i = 0; i < params.size(); i++) {
                    Array.set(array, i, params.get(i));
                }
            } else {
                for (int i = 0; i < params.size(); i++) {
                    Array.set(array, i, Convert.convert(componentType, params.get(i)));
                }
            }
            return array;
        } else if (targetClazz.isAssignableFrom(ArrayList.class)) {
            if (isSameType) {
                return new ArrayList<>(params);
            } else {
                ArrayList arrayList = new ArrayList<>();
                params.forEach(param -> arrayList.add(Convert.convert(types[0], param)));
                return arrayList;
            }
        } else if (targetClazz.isAssignableFrom(HashSet.class)) {
            if (isSameType) {
                return new HashSet<>(params);
            } else {
                HashSet set = new HashSet<>();
                params.forEach(param -> set.add(Convert.convert(types[0], param)));
                return new HashSet<>(set);
            }
        } else if (targetClazz.isAssignableFrom(LinkedList.class)) {
            if (isSameType) {
                return new LinkedList<>(params);
            } else {
                LinkedList linkedList = new LinkedList<>();
                params.forEach(param -> linkedList.add(Convert.convert(types[0], param)));
                return new LinkedList<>(linkedList);
            }
        }
        return params;
    }

}

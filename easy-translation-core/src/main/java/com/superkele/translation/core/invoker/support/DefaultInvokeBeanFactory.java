package com.superkele.translation.core.invoker.support;

import com.superkele.translation.core.util.Pair;

import java.util.Map;

public class DefaultInvokeBeanFactory extends AbstractInvokeBeanFactory {
    @Override
    protected String[] getBeanNames(Class<?> clazz) {
        String[] singleBeanNames = getSingleBeanNames(singleBeans, clazz);
        String[] prototypeBeanNames = getPrototypeBeanNames(prototypeBeans, clazz);
        String[] res = new String[singleBeanNames.length + prototypeBeanNames.length];
        int i = 0;
        while (i < singleBeanNames.length) {
            res[i] = singleBeanNames[i];
            i++;
        }
        while (i < res.length) {
            res[i] = prototypeBeanNames[i - singleBeanNames.length];
            i++;
        }
        return new String[0];
    }

    public String[] getSingleBeanNames(Map<String, Object> map, Class<?> clazz) {
        return map.values()
                .stream()
                .filter(obj -> clazz.isInstance(obj))
                .toArray(String[]::new);
    }

    public String[] getPrototypeBeanNames(Map<String, Pair<Class, Object>> map, Class<?> clazz) {
        return map.values()
                .stream()
                .filter(pair -> pair.getKey().isAssignableFrom(clazz))
                .toArray(String[]::new);
    }
}

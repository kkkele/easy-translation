package com.superkele.translation.core.invoker.support;

import cn.hutool.core.bean.BeanUtil;
import com.superkele.translation.core.exception.NotDefineException;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractInvokeBeanFactory extends DefaultInvokeBeanRegistry implements InvokeBeanFactory {

    @Override
    public Object getBean(String beanName) {
        if (singleBeans.containsKey(beanName)) {
            return singleBeans.get(beanName);
        }
        if (prototypeBeans.containsKey(beanName)) {
            Pair<Class, Object> pair = prototypeBeans.get(beanName);
            Object prototype = pair.getValue();
            Class targetClazz = pair.getKey();
            Object res = BeanUtil.copyProperties(prototype, targetClazz);
            return res;
        }
        return null;
    }

    @Override
    public Object getBean(Class<?> clazz) {
        String[] beanNames = getBeanNames(clazz);
        if (beanNames == null || beanNames.length == 0) {
            throw new NotDefineException("invoke bean type" + clazz.getName() + "not found");
        }
        if (beanNames.length == 1) {
            return getBean(beanNames[0]);
        }
        throw new TranslationException("find more than one invoke bean which type is" + clazz.getName());
    }

    @Override
    public Map<String, Object> getBeansOfType(Class<?> clazz) {
        String[] beanNames = getBeanNames(clazz);
        return Optional.ofNullable(beanNames)
                .map(v -> Arrays.stream(v)
                        .map(name -> Pair.of(name, getBean(name)))
                        .collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue())))
                .orElse(new HashMap<>());
    }

    protected abstract String[] getBeanNames(Class<?> clazz);

}

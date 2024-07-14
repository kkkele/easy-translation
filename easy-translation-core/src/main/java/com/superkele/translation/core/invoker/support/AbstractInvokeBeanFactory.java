package com.superkele.translation.core.invoker.support;

import cn.hutool.core.bean.BeanUtil;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.exception.NotDefineException;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.util.Pair;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractInvokeBeanFactory extends DefaultInvokeBeanRegistry implements ConfigurableInvokeBeanFactory {

    public List<Consumer<ConfigurableInvokeBeanFactory>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void refresh() {
        TransManager.getTransLog().info("InvokeBeanFactory load successfully");
        notice();
    }

    @Override
    public void notice() {
        listeners.forEach(listener -> listener.accept(this));
    }

    @Override
    public void addListener(Consumer<ConfigurableInvokeBeanFactory> action) {
        listeners.remove(action);
        listeners.add(action);
    }

    @Override
    public <T> T getBean(String beanName) {
        if (singleBeans.containsKey(beanName)) {
            return (T) singleBeans.get(beanName);
        }
        if (prototypeBeans.containsKey(beanName)) {
            Pair<Class, Object> pair = prototypeBeans.get(beanName);
            Object prototype = pair.getValue();
            Class targetClazz = pair.getKey();
            Object res = BeanUtil.copyProperties(prototype, targetClazz);
            return (T) res;
        }
        return null;
    }

    @Override
    public <T> T getBean(Class<?> clazz) {
        String[] beanNames = getBeanNames(clazz);
        if (beanNames == null || beanNames.length == 0) {
            throw new NotDefineException("invoke bean type" + clazz.getName() + "not found");
        }
        if (beanNames.length == 1) {
            return (T) getBean(beanNames[0]);
        }
        throw new TranslationException("find more than one invoke bean which type is" + clazz.getName());
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        String[] beanNames = getBeanNames(clazz);
        return Optional.ofNullable(beanNames)
                .map(v -> Arrays.stream(v)
                        .map(name -> Pair.of(name, getBean(name)))
                        .collect(Collectors.toMap(pair -> pair.getKey(), pair -> (T) pair.getValue())))
                .orElse(new HashMap<>());
    }

    protected abstract String[] getBeanNames(Class<?> clazz);

}

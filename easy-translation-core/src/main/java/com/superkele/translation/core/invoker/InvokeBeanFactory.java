package com.superkele.translation.core.invoker;

import java.util.Map;

/**
 * 获取invokeBean 的工厂
 * 用来提供转换翻译器的Bean， invokeObj + method => translator
 */
public interface InvokeBeanFactory {

    <T> T getBean(String beanName);

    <T> T getBean(Class<?> clazz);

    <T> Map<String, T> getBeansOfType(Class<T> clazz);
}

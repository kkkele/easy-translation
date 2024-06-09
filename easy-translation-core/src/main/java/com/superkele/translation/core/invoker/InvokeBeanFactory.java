package com.superkele.translation.core.invoker;

import java.util.Map;

/**
 * 获取invokeBean 的工厂
 * 用来提供转换翻译器的Bean， invokeObj + method => translator
 */
public interface InvokeBeanFactory {

    Object getBean(String beanName);

    Object getBean(Class<?> clazz);

    Map<String, Object> getBeansOfType(Class<?> clazz);
}

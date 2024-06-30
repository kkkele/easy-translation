package com.superkele.translation.core.translator.support;

import cn.hutool.core.util.ArrayUtil;
import com.superkele.translation.core.exception.TranslationException;
import com.superkele.translation.core.invoker.InvokeBeanFactory;
import com.superkele.translation.core.translator.definition.ConfigurableTranslatorDefinitionFactory;
import com.superkele.translation.core.translator.definition.TranslatorDefinition;
import com.superkele.translation.core.translator.definition.TranslatorDefinitionRegistry;
import com.superkele.translation.core.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultTranslatorFactory extends AbstractAutowireCapableTranslatorFactory
        implements ConfigurableTranslatorDefinitionFactory, TranslatorDefinitionRegistry {

    private final InvokeBeanFactory invokeBeanRegistry;
    private final Map<String, TranslatorDefinition> translatorDefinitionMap = new ConcurrentHashMap<>();

    public DefaultTranslatorFactory(InvokeBeanFactory invokeBeanRegistry) {
        Assert.notNull(invokeBeanRegistry, "InvokeBeanFactory must not be null");
        this.invokeBeanRegistry = invokeBeanRegistry;
    }

    @Override
    public void register(String translatorName, TranslatorDefinition definition) {
        translatorDefinitionMap.put(translatorName, definition);
    }

    @Override
    public String[] getTranslatorNames() {
        return ArrayUtil.toArray(translatorDefinitionMap.keySet(), String.class);
    }

    @Override
    public boolean containsTranslatorDefinition(String name) {
        return translatorDefinitionMap.containsKey(name);
    }


    @Override
    public TranslatorDefinition findTranslatorDefinition(String translatorName) {
        return Optional.ofNullable(translatorDefinitionMap.get(translatorName))
                .orElseThrow(() -> new TranslationException("No translator name '" + translatorName + "' is found"));
    }

    @Override
    protected Object getBeanInvoker(String beanName) {
        try {
            return invokeBeanRegistry.getBean(beanName);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected <T> T getBeanInvoker(Class<T> clazz) {
        try {
            Object bean = invokeBeanRegistry.getBean(clazz);
            return (T) bean;
        } catch (Exception e) {
        }
        Map<String, T> beansOfType = invokeBeanRegistry.getBeansOfType(clazz);
        Object defaultBean = null;
        for (Object bean : beansOfType.values()) {
            if (bean.getClass() == clazz) {
                return (T) bean;
            }
            defaultBean = bean;
        }
        return (T) defaultBean;
    }

}

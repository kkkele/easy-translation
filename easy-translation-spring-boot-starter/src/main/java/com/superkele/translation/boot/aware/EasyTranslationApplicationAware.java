package com.superkele.translation.boot.aware;

import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.ConfigurableTransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;
import com.superkele.translation.core.util.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


@Component("EasyTranslationApplicationAware")
public class EasyTranslationApplicationAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableTransExecutorContext executorContext = applicationContext.getBean("defaultTransExecutorContext", ConfigurableTransExecutorContext.class);
        if (executorContext instanceof DefaultTransExecutorContext) {
            DefaultTransExecutorContext defaultTransExecutorContext = (DefaultTransExecutorContext) executorContext;
            Config defaultTranslationConfig = applicationContext.getBean("defaultTranslationConfig", Config.class);
            String[] packages = TranslationGlobalInformation.getPackages().stream().toArray(String[]::new);
            DefaultTranslatorDefinitionReader reader = new DefaultTranslatorDefinitionReader(packages);
            Set<Class<?>> translatorDeclaringClasses = reader.getTranslatorDeclaringClasses();
            Object[] invokeObjs = translatorDeclaringClasses.stream()
                    .map(clazz -> applicationContext.getBeanNamesForType(clazz))
                    .flatMap(Arrays::stream)
                    .filter(beanName -> !"EasyTranslationApplicationAware".equals(beanName))
                    .map(applicationContext::getBean)
                    .toArray(Object[]::new);
            defaultTransExecutorContext.setDefinitionReader(reader);
            defaultTransExecutorContext.setConfig(defaultTranslationConfig);
            defaultTransExecutorContext.setInvokeObjs(invokeObjs);
        }
        executorContext.refresh();
        LogUtils.debug("executorContext refresh success");
    }
}

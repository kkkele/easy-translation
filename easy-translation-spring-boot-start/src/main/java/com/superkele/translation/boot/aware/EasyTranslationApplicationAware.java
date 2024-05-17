package com.superkele.translation.boot.aware;

import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.ConfigurableTransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.util.LogUtils;
import org.springframework.beans.BeansException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component("EasyTranslationApplicationAware")
@Cacheable
public class EasyTranslationApplicationAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableTransExecutorContext executorContext = applicationContext.getBean("defaultTransExecutorContext", ConfigurableTransExecutorContext.class);
        if (executorContext instanceof DefaultTransExecutorContext) {
            DefaultTransExecutorContext defaultTransExecutorContext = (DefaultTransExecutorContext) executorContext;
            Config defaultTranslationConfig = applicationContext.getBean("defaultTranslationConfig", Config.class);
            defaultTransExecutorContext.setConfig(defaultTranslationConfig);
            String[] packages = TranslationGlobalInformation.getPackages().stream().toArray(String[]::new);
            defaultTransExecutorContext.setPackages(packages);
            String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
            Object[] invokeObjs = Arrays.stream(beanDefinitionNames)
                    .filter(beanName -> !"EasyTranslationApplicationAware".equals(beanName))
                    .map(beanName -> applicationContext.getBean(beanName))
                    .toArray(Object[]::new);
            defaultTransExecutorContext.setInvokeObjs(invokeObjs);
        }
        executorContext.refresh();
        LogUtils.debug("executorContext refresh success");
    }
}

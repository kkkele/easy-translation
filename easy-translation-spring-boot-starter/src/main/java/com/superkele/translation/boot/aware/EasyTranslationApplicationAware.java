package com.superkele.translation.boot.aware;

import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.ConfigurableTranslatorContext;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.translator.support.DefaultTranslatorDefinitionReader;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;

@Slf4j
@Component("EasyTranslationApplicationAware")
public class EasyTranslationApplicationAware implements ApplicationContextAware {
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableTranslatorContext executorContext = applicationContext.getBean("defaultTransExecutorContext", ConfigurableTranslatorContext.class);
        if (executorContext instanceof DefaultTranslatorContext) {
            DefaultTranslatorContext defaultTransExecutorContext = (DefaultTranslatorContext) executorContext;
            Config defaultTranslationConfig = applicationContext.getBean("defaultTranslationConfig", Config.class);
            String[] packages = TranslationGlobalInformation.getPackages().stream().toArray(String[]::new);
            defaultTransExecutorContext.setBasePackages(packages);
            defaultTransExecutorContext.setConfig(defaultTranslationConfig);
            // todo 待完成 defaultTransExecutorContext.setInvokeBeanFactory();
        }
        executorContext.refresh();
        LogUtils.debug(log::debug,"executorContext refresh success");
    }
}

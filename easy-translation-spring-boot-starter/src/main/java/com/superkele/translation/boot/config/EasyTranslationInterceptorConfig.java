package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationConfig;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationInterceptorConfig implements ApplicationContextAware {

    private final TranslationConfig config;

    private final DefaultTranslatorContext defaultTransExecutorContext;

    private final DefaultTranslationProcessor defaultTranslationProcessor;


    @Autowired(required = false)
    public void customize(List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        if (configCustomizers != null)
            configCustomizers.forEach(customizer -> {
                customizer.customize(config);
                LogUtils.debug(log::debug, "add config-customizer: {}", () -> customizer);
            });
    }

    @Autowired(required = false)
    public void addTranslatorFactoryPostProcessor(List<TranslatorFactoryPostProcessor> postProcessors) {
        if (postProcessors != null)
            postProcessors.forEach(translatorFactoryPostProcessor -> {
                defaultTransExecutorContext.addTranslatorFactoryPostProcessor(translatorFactoryPostProcessor);
                LogUtils.debug(log::debug, "add translatorFactoryPostProcessor: {}", () -> postProcessors);
            });
    }

    @Autowired(required = false)
    public void addTranslatorPostProcessor(List<TranslatorPostProcessor> postProcessors) {
        if (postProcessors != null)
            postProcessors.forEach(postProcessor -> {
                defaultTransExecutorContext.addTranslatorPostProcessor(postProcessor);
                LogUtils.debug(log::debug, "add translatorPostProcessor: {}", () -> postProcessor);
            });
    }

    @Autowired(required = false)
    public void addContextHolder(List<ContextHolder<Object>> contextHolders) {
        if (contextHolders != null)
            contextHolders.forEach(contextHolder -> {
                defaultTranslationProcessor.addContextHolder(contextHolder);
                LogUtils.debug(log::debug, "add contextHolder: {}", () -> contextHolder);
            });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.defaultTransExecutorContext.refresh();
    }
}

package com.superkele.translation.boot.config;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class EasyTranslationInterceptorConfig {


    @Autowired(required = false)
    public void customize(Config config, List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        if (configCustomizers != null)
            configCustomizers.forEach(customizer -> {
                customizer.customize(config);
                LogUtils.debug(log::debug,"add config-customizer: {}", () -> customizer);
            });
    }

    @Autowired(required = false)
    public void addTranslatorFactoryPostProcessor(DefaultTranslatorContext defaultTransExecutorContext, List<TranslatorFactoryPostProcessor> postProcessors) {
        if (postProcessors != null)
            postProcessors.forEach(translatorFactoryPostProcessor -> {
                defaultTransExecutorContext.addTranslatorFactoryPostProcessor(translatorFactoryPostProcessor);
                LogUtils.debug(log::debug,"add translatorFactoryPostProcessor: {}", () -> postProcessors);
            });
    }

    @Autowired(required = false)
    public void addTranslatorPostProcessor(DefaultTranslatorContext defaultTransExecutorContext, List<TranslatorPostProcessor> postProcessors) {
        if (postProcessors != null)
            postProcessors.forEach(postProcessor -> {
                defaultTransExecutorContext.addTranslatorPostProcessor(postProcessor);
                LogUtils.debug(log::debug,"add translatorPostProcessor: {}", () -> postProcessor);
            });
    }

    @Autowired(required = false)
    public void addContextHandler(DefaultTranslationProcessor defaultTranslationProcessor, List<ContextHolder> contextPassers) {
        if (contextPassers != null)
            contextPassers.forEach(contextPasser -> {
                defaultTranslationProcessor.addContextHolders(contextPasser);
                LogUtils.debug(log::debug,"add contextHolder: {}", () -> contextPasser);
            });
    }

}

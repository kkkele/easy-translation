package com.superkele.translation.boot.config;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.thread.ContextPasser;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class EasyTranslationInterceptorConfig {


    @Autowired(required = false)
    public void customize(Config config, List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        if (configCustomizers != null)
            configCustomizers.forEach(customizer -> {
                customizer.customize(config);
                LogUtils.debug("add config-customizer: {}", () -> customizer);
            });
    }

    @Autowired(required = false)
    public void addTranslatorFactoryPostProcessor(DefaultTransExecutorContext defaultTransExecutorContext, List<TranslatorFactoryPostProcessor> postProcessors) {
        if (postProcessors != null)
            postProcessors.forEach(translatorFactoryPostProcessor -> {
                defaultTransExecutorContext.addTranslatorFactoryPostProcessor(translatorFactoryPostProcessor);
                LogUtils.debug("add translatorFactoryPostProcessor: {}", () -> postProcessors);
            });
    }

    @Autowired(required = false)
    public void addTranslatorPostProcessor(DefaultTransExecutorContext defaultTransExecutorContext, List<TranslatorPostProcessor> postProcessors) {
        if (postProcessors != null)
            postProcessors.forEach(postProcessor -> {
                defaultTransExecutorContext.getTranslatorFactory()
                        .addTranslatorPostProcessor(postProcessor);
                LogUtils.debug("add translatorPostProcessor: {}", () -> postProcessor);
            });
    }

    @Autowired(required = false)
    public void addContextHandler(DefaultTranslationProcessor defaultTranslationProcessor, List<ContextHolder> contextPassers) {
        if (contextPassers != null)
            contextPassers.forEach(contextPasser -> {
                defaultTranslationProcessor.addContextHolders(contextPasser);
                LogUtils.debug("add contextHolder: {}", () -> contextPasser);
            });
    }

}

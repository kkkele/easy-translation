package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
public class EasyTranslationInterceptorConfig {

    @Autowired(required = false)
    public void setLog(TranslationProperties properties) {
        Optional.ofNullable(properties)
                .ifPresent(p -> {
                    LogUtils.printLog = p.isDebug();
                    LogUtils.debug(log::debug,"debug mode enabled");
                });
    }

    @Autowired(required = false)
    public void customize(List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        if (configCustomizers != null)
            configCustomizers.forEach(customizer -> {
                customizer.customize(Config.INSTANCE);
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
    public void addContextHandler(List<ContextHolder> contextPassers) {
        if (contextPassers != null)
            contextPassers.forEach(contextPasser -> {
                Config.INSTANCE.addContextHolders(contextPasser);
                LogUtils.debug(log::debug,"add contextHolder: {}", () -> contextPasser);
            });
    }


/*    @Autowired(required = false)
    public void customizer(ObjectMapper objectMapper, TranslationJsonNodeModule translationFieldSerializeModifier) {
        objectMapper.registerModules(translationFieldSerializeModifier);
    }*/

}

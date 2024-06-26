package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationConfig;
import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import com.superkele.translation.core.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationInterceptorConfig {

    private final TranslationConfig config;

    private final DefaultTranslatorContext defaultTransExecutorContext;

    private final DefaultTranslationProcessor defaultTranslationProcessor;

    @Autowired
    public void setLog(TranslationConfig properties) {
        Optional.ofNullable(properties)
                .ifPresent(p -> {
                    LogUtils.printLog = p.isDebug();
                    LogUtils.debug(log::debug, "debug mode enabled");
                    Set<String> translator = properties.getBasePackage().getTranslator();
                    TranslationGlobalInformation.addTranslatorPackage(translator);
                    Set<String> domain = properties.getBasePackage().getDomain();
                    TranslationGlobalInformation.addDomainPackage(domain);
                });
    }

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
    public void addContextHolder(List<ContextHolder> contextHolders) {
        if (contextHolders != null)
            contextHolders.forEach(contextHolder -> {
                defaultTranslationProcessor.addContextHolder(contextHolder);
                LogUtils.debug(log::debug, "add contextHolder: {}", () -> contextHolder);
            });
    }

}

package com.superkele.translation.boot.config;


import com.superkele.translation.boot.aware.EasyTranslationApplicationAware;
import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import com.superkele.translation.core.aop.TranslationAspect;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.context.ConfigurableTransExecutorContext;
import com.superkele.translation.core.context.support.DefaultTransExecutorContext;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.util.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@EnableConfigurationProperties({TranslationProperties.class})
@RequiredArgsConstructor
@Configuration
public class EasyTranslationBaseConfig {

    @Autowired
    public void setLog(TranslationProperties properties) {
        Optional.ofNullable(properties)
                .ifPresent(p -> {
                    LogUtils.printLog = p.isDebug();
                    LogUtils.debug("debug mode enabled");
                });
    }

    @Bean
    public TranslationScanPostProcessor translationScanPostProcessor() {
        return new TranslationScanPostProcessor();
    }

    @Bean
    public Config defaultTranslationConfig() {
        Config config = new Config();
        return config;
    }

    @Bean
    public ConfigurableTransExecutorContext defaultTransExecutorContext() {
        return new DefaultTransExecutorContext();
    }

    @Bean
    public TranslationProcessor defaultTranslationProcessor(ConfigurableTransExecutorContext defaultTransExecutorContext, Config defaultTranslationConfig) {
        return new DefaultTranslationProcessor(defaultTransExecutorContext, defaultTranslationConfig);
    }

    @Bean
    public TranslationAspect translationAspect(TranslationProcessor defaultTranslationProcessor) {
        return new TranslationAspect(defaultTranslationProcessor);
    }
}

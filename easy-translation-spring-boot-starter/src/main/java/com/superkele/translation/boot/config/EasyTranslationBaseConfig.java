package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import com.superkele.translation.core.aop.TranslationAspect;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.util.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@EnableConfigurationProperties({TranslationProperties.class})
@RequiredArgsConstructor
@Configuration
@Slf4j
public class EasyTranslationBaseConfig {

    @Autowired
    public void setLog(TranslationProperties properties) {
        Optional.ofNullable(properties)
                .ifPresent(p -> {
                    LogUtils.printLog = p.isDebug();
                    LogUtils.debug(log::debug,"debug mode enabled");
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
    public DefaultTranslatorContext defaultTransExecutorContext() {
        return new DefaultTranslatorContext();
    }

    @Bean
    public DefaultTranslationProcessor defaultTranslationProcessor(DefaultTranslatorContext defaultTransExecutorContext, Config defaultTranslationConfig) {
        return new DefaultTranslationProcessor(defaultTransExecutorContext, defaultTranslationConfig);
    }

    @Bean
    public TranslationAspect translationAspect(TranslationProcessor defaultTranslationProcessor) {
        return new TranslationAspect(defaultTranslationProcessor);
    }
}

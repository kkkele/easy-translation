package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import com.superkele.translation.core.aop.TranslationAspect;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.property.support.DefaultMethodHandlePropertyHandler;
import com.superkele.translation.core.property.PropertyHandler;
import com.superkele.translation.extension.serialize.jackson.TranslationJsonNodeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(TranslationProperties.class)
@RequiredArgsConstructor
@Configuration
@Slf4j
public class EasyTranslationBaseConfig {


    @Bean
    public TranslationScanPostProcessor translationScanPostProcessor() {
        return new TranslationScanPostProcessor();
    }

    @Bean
    public DefaultTranslatorContext defaultTransExecutorContext() {
        return new DefaultTranslatorContext();
    }

    @Bean
    public PropertyHandler defaultPropertyHandler() {
        return new DefaultMethodHandlePropertyHandler();
    }

    @Bean
    public DefaultTranslationProcessor defaultTranslationProcessor(DefaultTranslatorContext defaultTransExecutorContext, PropertyHandler propertyHandler) {
        return new DefaultTranslationProcessor(defaultTransExecutorContext, propertyHandler);
    }

    @Bean
    public TranslationJsonNodeModule translationFieldSerializeModifier(DefaultTranslatorContext defaultTransExecutorContext, PropertyHandler propertyHandler) {
        return new TranslationJsonNodeModule(defaultTransExecutorContext, propertyHandler);
    }


    @Bean
    public TranslationAspect translationAspect(TranslationProcessor defaultTranslationProcessor) {
        return new TranslationAspect(defaultTranslationProcessor);
    }
}

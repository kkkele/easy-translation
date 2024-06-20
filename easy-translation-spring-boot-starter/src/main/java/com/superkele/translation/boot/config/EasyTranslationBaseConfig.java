package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import com.superkele.translation.core.aop.TranslationAspect;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.support.MappingFiledTranslationBuilder;
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
    public DefaultTranslatorContext defaultTranslatorContext() {
        return new DefaultTranslatorContext();
    }

    @Bean
    public PropertyHandler defaultPropertyHandler() {
        return new DefaultMethodHandlePropertyHandler();
    }

    @Bean
    public FieldTranslationBuilder mappingFiledTranslationBuilder() {
        return new MappingFiledTranslationBuilder();
    }

    @Bean
    public DefaultTranslationProcessor defaultTranslationProcessor(DefaultTranslatorContext defaultTransExecutorContext, FieldTranslationBuilder mappingFiledTranslationBuilder) {
        return new DefaultTranslationProcessor(defaultTransExecutorContext, mappingFiledTranslationBuilder);
    }

    @Bean
    public TranslationJsonNodeModule translationFieldSerializeModifier(DefaultTranslatorContext defaultTransExecutorContext, FieldTranslationBuilder fieldTranslationBuilder) {
        return new TranslationJsonNodeModule(defaultTransExecutorContext, fieldTranslationBuilder);
    }


    @Bean
    public TranslationAspect translationAspect(TranslationProcessor defaultTranslationProcessor) {
        return new TranslationAspect(defaultTranslationProcessor);
    }
}

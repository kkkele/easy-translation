package com.superkele.translation.boot.config;

import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.boot.invoker.SpringInvokeBeanFactory;
import com.superkele.translation.boot.resolver.SpringParamHandlerResolver;
import com.superkele.translation.boot.resolver.SpringResultHandlerResolver;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import com.superkele.translation.core.aop.TranslationAspect;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.mapping.support.DefaultParamHandler;
import com.superkele.translation.core.mapping.support.DefaultResultHandler;
import com.superkele.translation.core.metadata.support.DefaultConfigurableFieldTranslationFactory;
import com.superkele.translation.core.metadata.support.DefaultMappingFiledTranslationBuilder;
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.util.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationConfig {

    @Bean
    public TranslationScanPostProcessor translationScanPostProcessor() {
        return new TranslationScanPostProcessor();
    }


    @Bean
    @ConditionalOnMissingBean
    public SpringInvokeBeanFactory springInvokeBeanFactory() {
        return new SpringInvokeBeanFactory();
    }

    @Bean
    public DefaultTranslatorContext defaultTranslatorContext(Config config,SpringInvokeBeanFactory springInvokeBeanFactory) {
        String[] scanPackages = TranslationGlobalInformation.getTranslatorPackages()
                .stream().toArray(String[]::new);
        return new DefaultTranslatorContext(config,springInvokeBeanFactory, scanPackages);
    }

    @Bean
    public SpringParamHandlerResolver springParamHandlerResolver() {
        return new SpringParamHandlerResolver();
    }

    @Bean
    public SpringResultHandlerResolver springResultHandlerResolver() {
        return new SpringResultHandlerResolver();
    }
    @Bean
    public DefaultParamHandler defaultParamHandler() {
        return Singleton.get(DefaultParamHandler.class);
    }

    @Bean
    public DefaultResultHandler defaultResultHandler() {
        return Singleton.get(DefaultResultHandler.class);
    }

    @Bean
    public DefaultMappingFiledTranslationBuilder defaultMappingFiledTranslationBuilder(DefaultTranslatorContext defaultTransExecutorContext,
                                                                                       SpringParamHandlerResolver parameterHandlerResolver,
                                                                                       SpringResultHandlerResolver resultHandlerResolver) {
        return new DefaultMappingFiledTranslationBuilder(defaultTransExecutorContext.getTranslatorFactory(), parameterHandlerResolver, resultHandlerResolver);
    }

    @Bean
    public DefaultConfigurableFieldTranslationFactory defaultConfigurableFieldTranslationFactory(DefaultMappingFiledTranslationBuilder defaultMappingFiledTranslationBuilder) {
        return new DefaultConfigurableFieldTranslationFactory(defaultMappingFiledTranslationBuilder);
    }

    @Bean
    public DefaultTranslationProcessor defaultTranslationProcessor(DefaultTranslatorContext defaultTransExecutorContext,
                                                                   DefaultConfigurableFieldTranslationFactory defaultConfigurableFieldTranslationFactory,
                                                                   Config config) {
        return new DefaultTranslationProcessor(defaultTransExecutorContext,defaultConfigurableFieldTranslationFactory,config);
    }

    @Bean
    public TranslationAspect translationAspect(TranslationProcessor defaultTranslationProcessor) {
        return new TranslationAspect(defaultTranslationProcessor);
    }
}

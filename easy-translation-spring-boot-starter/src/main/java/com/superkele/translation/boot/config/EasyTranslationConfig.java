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
import com.superkele.translation.core.processor.TranslationProcessor;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.util.LogUtils;
import com.superkele.translation.core.util.Singleton;
import com.superkele.translation.extension.serialize.jackson.JacksonWriteAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationConfig {



    @Bean
    public TranslationScanPostProcessor translationScanPostProcessor() {
        LogUtils.debug(log::debug, "translationScanPostProcessor init");
        return new TranslationScanPostProcessor();
    }


    @Bean
    public SpringInvokeBeanFactory springInvokeBeanFactory() {
        LogUtils.debug(log::debug, "springInvokeBeanFactory init");
        return new SpringInvokeBeanFactory();
    }

    @Bean
    public DefaultTranslatorContext defaultTranslatorContext(Config config, SpringInvokeBeanFactory springInvokeBeanFactory) {
        String[] scanPackages = TranslationGlobalInformation.getTranslatorPackages()
                .stream().toArray(String[]::new);
        LogUtils.debug(log::debug, "defaultTranslatorContext init");
        return new DefaultTranslatorContext(config, springInvokeBeanFactory, scanPackages);
    }

    @Bean
    public SpringParamHandlerResolver springParamHandlerResolver() {
        LogUtils.debug(log::debug, "springParamHandlerResolver init");
        return new SpringParamHandlerResolver();
    }

    @Bean
    public SpringResultHandlerResolver springResultHandlerResolver() {
        LogUtils.debug(log::debug, "springResultHandlerResolver init");
        return new SpringResultHandlerResolver();
    }

    @Bean
    public DefaultParamHandler defaultParamHandler() {
        LogUtils.debug(log::debug, "defaultParamHandler init");
        return Singleton.get(DefaultParamHandler.class);
    }

    @Bean
    public DefaultResultHandler defaultResultHandler() {
        LogUtils.debug(log::debug, "defaultResultHandler init");
        return Singleton.get(DefaultResultHandler.class);
    }

    @Bean
    public DefaultConfigurableFieldTranslationFactory defaultConfigurableFieldTranslationFactory(DefaultTranslatorContext defaultTranslatorContext,
                                                                                                 SpringParamHandlerResolver parameterHandlerResolver,
                                                                                                 SpringResultHandlerResolver resultHandlerResolver) {
        String[] domainPackages = TranslationGlobalInformation.getDomainPackages()
                .stream().toArray(String[]::new);
        LogUtils.debug(log::debug, "defaultConfigurableFieldTranslationFactory init");
        return new DefaultConfigurableFieldTranslationFactory(defaultTranslatorContext, parameterHandlerResolver, resultHandlerResolver, domainPackages);
    }

    @Bean
    public DefaultTranslationProcessor defaultTranslationProcessor(DefaultTranslatorContext defaultTransExecutorContext,
                                                                   DefaultConfigurableFieldTranslationFactory defaultConfigurableFieldTranslationFactory,
                                                                   Config config) {
        LogUtils.debug(log::debug, "defaultTranslationProcessor init");
        return new DefaultTranslationProcessor(defaultTransExecutorContext, defaultConfigurableFieldTranslationFactory, config);
    }

    @Bean
    public JacksonWriteAspect jacksonWriteAspect(){
        LogUtils.debug(log::debug, "JacksonWriteAspect init");
        return new JacksonWriteAspect();
    }

    @Bean
    public TranslationAspect translationAspect(TranslationProcessor defaultTranslationProcessor) {
        LogUtils.debug(log::debug, "translationAspect init");
        return new TranslationAspect(defaultTranslationProcessor);
    }

}

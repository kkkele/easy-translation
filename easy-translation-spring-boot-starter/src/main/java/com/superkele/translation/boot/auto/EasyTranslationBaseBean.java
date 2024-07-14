package com.superkele.translation.boot.auto;

import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.aop.TranslationAspect;
import com.superkele.translation.core.context.FieldTranslationInfoContext;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.mapping.support.DefaultParamHandler;
import com.superkele.translation.core.mapping.support.DefaultResultHandler;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.util.Singleton;
import com.superkele.translation.extension.serialize.jackson.JacksonWriteAspect;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


@Slf4j
@RequiredArgsConstructor
@AutoConfiguration(after = EasyTranslationInitConfig.class)
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationBaseBean {

    @Bean
    public DefaultTranslatorContext defaultTranslatorContext() {
        return TransManager.getTranslatorContext();
    }

    @Bean
    public FieldTranslationInfoContext defaultConfigurableFieldTranslationFactory() {
        return TransManager.getFieldTranslationInfoContext();
    }

    @Bean
    public DefaultTranslationProcessor defaultTranslationProcessor() {
        return TransManager.getTranslationProcessor();
    }

    @Bean
    public TranslationAspect translationAspect() {
        return new TranslationAspect();
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
    @ConditionalOnProperty(prefix = "easy-translation", name = {"json-serialize"}, havingValue = "true")
    public JacksonWriteAspect jacksonWriteAspect() {
        return new JacksonWriteAspect();
    }

}

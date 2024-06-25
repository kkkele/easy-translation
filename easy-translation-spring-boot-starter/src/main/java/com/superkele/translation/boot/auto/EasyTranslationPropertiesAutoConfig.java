package com.superkele.translation.boot.auto;


import com.superkele.translation.boot.config.EasyTranslationInterceptorConfig;
import com.superkele.translation.boot.config.properties.TranslationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@EnableConfigurationProperties(TranslationProperties.class)
public class EasyTranslationPropertiesAutoConfig {

    @Bean
    @ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
    public EasyTranslationInterceptorConfig easyTranslationInterceptorAutoConfig() {
        return new EasyTranslationInterceptorConfig();
    }
}

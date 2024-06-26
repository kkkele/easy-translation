package com.superkele.translation.boot.auto;


import com.superkele.translation.boot.config.properties.TranslationConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@AutoConfiguration
@EnableConfigurationProperties(TranslationConfig.class)
public class EasyTranslationPropertiesAutoConfig {

}

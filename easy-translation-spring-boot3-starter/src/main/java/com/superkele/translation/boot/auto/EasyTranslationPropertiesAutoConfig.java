package com.superkele.translation.boot.auto;


import com.superkele.translation.boot.config.properties.TranslationBootConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(TranslationBootConfig.class)
public class EasyTranslationPropertiesAutoConfig {


}

package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({TranslationProperties.class})
@RequiredArgsConstructor
@Configuration
public class TranslationConfig {

    @Bean
    public TranslationScanPostProcessor translationScanPostProcessor() {
        return new TranslationScanPostProcessor();
    }

}

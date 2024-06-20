package com.superkele.demo.config;


import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@RequiredArgsConstructor
public class TranslationConfig {

    private final ThreadPoolTaskExecutor threadPoolExecutor;

    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setThreadPoolExecutor(threadPoolExecutor);
        };
    }
}

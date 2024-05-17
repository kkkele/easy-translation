package com.superkele.demo.config;


import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
@RequiredArgsConstructor
@EnableCaching
public class EasyTranslationConfig {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Bean
    public TranslationAutoConfigurationCustomizer translationAutoConfigurationCustomizer() {
        return config -> {
            config.setTimeout(1000)
                    .setThreadPoolExecutor(threadPoolTaskExecutor.getThreadPoolExecutor());
        };
    }


}

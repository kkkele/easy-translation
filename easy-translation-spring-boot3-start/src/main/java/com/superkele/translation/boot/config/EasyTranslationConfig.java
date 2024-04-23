package com.superkele.translation.boot.config;


import cn.hutool.core.collection.CollectionUtil;
import com.superkele.translation.core.config.Config;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@RequiredArgsConstructor
public class EasyTranslationConfig {

    @Bean
    public Config config(@Autowired(required = false) List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        Config config = new Config();
        if (CollectionUtil.isNotEmpty(configCustomizers)) {
            configCustomizers.forEach(customizer -> customizer.customize(config));
        }
        return config;
    }
}

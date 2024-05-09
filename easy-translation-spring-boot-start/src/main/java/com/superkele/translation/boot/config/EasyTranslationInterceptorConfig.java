package com.superkele.translation.boot.config;


import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
public class EasyTranslationInterceptorConfig {


    @Autowired(required = false)
    public void CustomizeTranslationInterceptor(Config config, List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        Optional.ofNullable(configCustomizers)
                .ifPresent(customizers -> customizers.forEach(customizer -> {
                    customizer.customize(config);
                    LogUtils.debug("add config-customizer: {}", () -> customizer);
                }));
    }
}

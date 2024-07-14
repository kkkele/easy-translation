package com.superkele.translation.boot.auto;


import com.superkele.translation.boot.invoker.SpringInvokeBeanFactory;
import com.superkele.translation.boot.scanner.TranslationScanPostProcessor;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.config.TranslationAutoConfigurationCustomizer;
import com.superkele.translation.core.config.TranslationConfig;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.mapping.ParamHandlerResolver;
import com.superkele.translation.core.mapping.ResultHandlerResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@Slf4j
@AutoConfiguration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationInitConfig {

    private final TranslationConfig config;

    @Bean
    public TranslationScanPostProcessor translationScanPostProcessor() {
        return new TranslationScanPostProcessor();
    }

    @Autowired(required = false)
    public void customize(List<TranslationAutoConfigurationCustomizer> configCustomizers) {
        TransManager.setConfig(config);
        TransLog transLog = TransManager.getTransLog();
        Optional.ofNullable(configCustomizers)
                .ifPresent(customizers -> customizers.forEach(customizer -> {
                    customizer.customize(config);
                    transLog.debug("add config-customizer: {}", () -> customizer);
                }));
        transLog.debug("TranslationConfig:{}", () -> config);
    }

    @Autowired(required = false)
    public void setInvokeBeanFactory(SpringInvokeBeanFactory invokeBeanFactory) {
        TransManager.setInvokeBeanFactory(invokeBeanFactory);
    }

    @Autowired(required = false)
    public void setParamHandlerResolver(ParamHandlerResolver paramHandlerResolver) {
        TransManager.setParamHandlerResolver(paramHandlerResolver);
    }

    @Autowired(required = false)
    public void setResultHandlerResolver(ResultHandlerResolver resultHandlerResolver) {
        TransManager.setResultHandlerResolver(resultHandlerResolver);
    }

}

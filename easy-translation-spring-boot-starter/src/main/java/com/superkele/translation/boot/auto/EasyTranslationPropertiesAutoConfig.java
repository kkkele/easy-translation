package com.superkele.translation.boot.auto;


import com.superkele.translation.boot.config.properties.TranslationConfig;
import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.Optional;
import java.util.Set;


@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(TranslationConfig.class)
public class EasyTranslationPropertiesAutoConfig {


    @Autowired
    public void setLog(TranslationConfig properties) {
        Optional.ofNullable(properties)
                .ifPresent(p -> {
                    LogUtils.printLog = p.isDebug();
                    LogUtils.debug(log::debug, "debug mode enabled");
                    Set<String> translator = properties.getBasePackage().getTranslator();
                    TranslationGlobalInformation.addTranslatorPackage(translator);
                    Set<String> domain = properties.getBasePackage().getDomain();
                    TranslationGlobalInformation.addDomainPackage(domain);
                });
    }
}

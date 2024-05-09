package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.core.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class LogAutowiredConfig {

    @Autowired(required = false)
    public void setLog(TranslationProperties properties) {
        Optional.ofNullable(properties)
                .ifPresent(p -> {
                    LogUtils.printLog = p.isDebug();
                    LogUtils.debug("debug mode enabled");
                });
    }
}

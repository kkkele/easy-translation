package com.superkele.translation.boot.config;


import com.superkele.translation.boot.config.properties.TranslationProperties;
import com.superkele.translation.core.util.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogAutowiredConfig {

    @Autowired(required = false)
    public void setLog(TranslationProperties properties){
        if (properties != null){
            LogUtils.printLog = properties.isDebug();
            LogUtils.debug("debug mode enabled");
        }
    }
}

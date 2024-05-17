package com.superkele.translation.boot.config.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "easy-translation")
public class TranslationProperties {

    private boolean debug = false;

    public boolean isDebug() {
        return debug;
    }

    public TranslationProperties setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

}

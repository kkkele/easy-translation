package com.superkele.translation.boot.config.properties;


import com.superkele.translation.core.config.TranslationConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;
import java.util.Set;


@ConfigurationProperties(prefix = "easy-translation")
public class TranslationBootConfig extends TranslationConfig {

    /**
     * 是否开启翻译功能
     */
    private boolean enable = true;

    /**
     * 是否开启json序列化时翻译功能
     */
    private boolean jsonSerialize = true;

    /**
     * 扫描的包
     */
    private BasePackage basePackage = new BasePackage();

    public boolean isEnable() {
        return enable;
    }

    public TranslationBootConfig setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }


    public boolean isJsonSerialize() {
        return jsonSerialize;
    }

    public TranslationBootConfig setJsonSerialize(boolean jsonSerialize) {
        this.jsonSerialize = jsonSerialize;
        return this;
    }

    public BasePackage getBasePackage() {
        return basePackage;
    }

    public TranslationBootConfig setBasePackage(BasePackage basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public class BasePackage {

        /**
         * 被翻译类所在的包
         */
        private Set<String> domain;

        /**
         * 翻译器所在的包
         */
        private Set<String> translator;


        public Set<String> getDomain() {
            return domain;
        }

        public BasePackage setDomain(Set<String> domain) {
            this.domain = domain;
            Optional.ofNullable(this.domain)
                    .ifPresent(domainPackages -> {
                        setDomainPackages(domainPackages.stream().toArray(String[]::new));
                    });
            return this;
        }

        public Set<String> getTranslator() {
            return translator;
        }

        public BasePackage setTranslator(Set<String> translator) {
            this.translator = translator;
            Optional.ofNullable(this.translator)
                    .ifPresent(translatorPackages -> {
                        setTranslatorPackages(translatorPackages.stream().toArray(String[]::new));
                    });
            return this;
        }
    }
}

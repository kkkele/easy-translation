package com.superkele.translation.boot.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;


@Data
@ConfigurationProperties(prefix = "easy-translation")
public class TranslationProperties {

    /**
     * 是否开启翻译功能
     */
    private boolean enable = true;

    /**
     * 是否开启debug模式
     */
    private boolean debug = false;

    /**
     * 是否开启json序列化时翻译功能
     */
    private boolean jsonSerialize = true;

    /**
     * 扫描的包
     */
    private BasePackage basePackage = new BasePackage();


    @Data
    public static class BasePackage {

        /**
         * 被翻译类所在的包
         */
        private Set<String> domain;

        /**
         * 翻译器所在的包
         */
        private Set<String> translator;
    }
}

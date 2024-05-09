package com.superkele.translation.boot.config;


import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;


public class EnableTranslationImportSelector implements ImportSelector {

    private static final String TRANSLATION_BASE_CONFIG = TranslationConfig.class.getName();
    private static final String LOG_CONFIG = LogAutowiredConfig.class.getName();

    public EnableTranslationImportSelector() {
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{TRANSLATION_BASE_CONFIG,
                LOG_CONFIG};
    }


}

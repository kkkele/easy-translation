package com.superkele.translation.boot.selector;


import com.superkele.translation.boot.aware.EasyTranslationApplicationAware;
import com.superkele.translation.boot.config.EasyTranslationBaseConfig;
import com.superkele.translation.boot.config.EasyTranslationInterceptorConfig;
import com.superkele.translation.boot.invoker.SpringInvokeBeanFactory;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;


public class EnableTranslationImportSelector implements ImportSelector {


    public EnableTranslationImportSelector() {
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{EasyTranslationBaseConfig.class.getName(),
                EasyTranslationInterceptorConfig.class.getName(),
                EasyTranslationApplicationAware.class.getName(),
                SpringInvokeBeanFactory.class.getName()
        };
    }


}

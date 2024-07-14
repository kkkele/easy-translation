package com.superkele.translation.boot.scanner;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.annotation.TranslatorScan;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.config.TranslationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.List;

@Slf4j
public class TranslationScanPostProcessor implements BeanPostProcessor {


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        TranslationConfig translationConfig = TransManager.getConfig();
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(SpringBootApplication.class)){
            translationConfig.addTranslatorPackage(clazz.getPackage().getName());
            translationConfig.addDomainPackage(clazz.getPackage().getName());
        }
        TranslatorScan translatorScan = AnnotatedElementUtils.getMergedAnnotation(clazz, TranslatorScan.class);
        if (translatorScan != null) {
            translationConfig.addTranslatorPackage(clazz.getPackage().getName());
            TransManager.getTransLog().debug("增加包扫描:{}", () -> {
                List<String> packages = ListUtil.toList(translatorScan.basePackages());
                return packages;
            });
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}

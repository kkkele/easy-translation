package com.superkele.translation.boot.scanner;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.annotation.TranslatorScan;
import com.superkele.translation.boot.global.TranslationGlobalInformation;
import com.superkele.translation.core.util.LogUtils;
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
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(SpringBootApplication.class)){
            TranslationGlobalInformation.addTranslatorPackage(clazz.getPackage().getName());
        }
        TranslatorScan translatorScan = AnnotatedElementUtils.getMergedAnnotation(clazz, TranslatorScan.class);
        if (translatorScan != null) {
            TranslationGlobalInformation.addTranslatorPackage(translatorScan.basePackages());
            TranslationGlobalInformation.addTranslatorPackage(clazz.getPackage().getName());
            LogUtils.debug(log::debug,"增加包扫描:{}", () -> {
                List<String> packages = ListUtil.toList(translatorScan.basePackages());
                packages.add(clazz.getPackage().getName());
                return packages;
            });
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}

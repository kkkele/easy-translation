package com.superkele.translation.boot.scanner;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.annotation.TranslatorScan;
import com.superkele.translation.boot.global.TranslationGlobalInformation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.List;


public class TranslationScanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        TranslatorScan translatorScan = AnnotatedElementUtils.getMergedAnnotation(clazz, TranslatorScan.class);
        if (translatorScan != null) {
            TranslationGlobalInformation.addPackage(translatorScan.basePackages());
            TranslationGlobalInformation.addPackage(clazz.getPackage().getName());
            LogUtils.debug("增加包扫描:{}", () -> {
                List<String> packages = ListUtil.toList(translatorScan.basePackages());
                packages.add(clazz.getPackage().getName());
                return packages;
            });
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}

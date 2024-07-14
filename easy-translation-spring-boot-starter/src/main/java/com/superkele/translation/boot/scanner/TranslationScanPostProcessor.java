package com.superkele.translation.boot.scanner;

import cn.hutool.core.collection.ListUtil;
import com.superkele.translation.annotation.TranslatorScan;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.context.DynamicTranslatorContext;
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
        DynamicTranslatorContext translatorContext = TransManager.getTranslatorContext();
        Class<?> clazz = bean.getClass();
        if (clazz.isAnnotationPresent(SpringBootApplication.class)){
            translatorContext.loadExtract(clazz.getPackage().getName());
        }
        TranslatorScan translatorScan = AnnotatedElementUtils.getMergedAnnotation(clazz, TranslatorScan.class);
        if (translatorScan != null) {
            translatorContext.loadExtract(translatorScan.basePackages());
            TransManager.getTransLog().debug("增加包扫描:{}", () -> {
                List<String> packages = ListUtil.toList(translatorScan.basePackages());
                return packages;
            });
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

}

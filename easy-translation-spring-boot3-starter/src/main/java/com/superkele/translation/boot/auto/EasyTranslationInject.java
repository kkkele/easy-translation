package com.superkele.translation.boot.auto;

import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.core.thread.ContextHolder;
import com.superkele.translation.core.translator.definition.TranslatorFactoryPostProcessor;
import com.superkele.translation.core.translator.definition.TranslatorPostProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;
import java.util.Optional;

@Slf4j
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = "enable", havingValue = "true")
public class EasyTranslationInject {


    @Autowired(required = false)
    public void setTransLog(TransLog transLog) {
        TransManager.setTransLog(transLog);
    }

    @Autowired
    public void addContextHolder(DefaultTranslationProcessor translationProcessor, @Autowired(required = false) List<ContextHolder> contextHolderList) {
        Optional.ofNullable(contextHolderList)
                .ifPresent(list -> list.forEach(contextHolder -> translationProcessor.addContextHolder(contextHolder)));
    }

    @Autowired
    public void addTranslatorPostProcessor(DefaultTranslatorContext translatorContext, @Autowired(required = false) List<TranslatorPostProcessor> postProcessors) {
        TransLog transLog = TransManager.getTransLog();
        Optional.ofNullable(postProcessors)
                .ifPresent(param -> param.forEach(item -> {
                    translatorContext.addTranslatorPostProcessor(item);
                    transLog.debug("add translatorFactoryPostProcessor: {}", () -> item);
                }));
    }

    @Autowired
    public void addTranslatorFactoryPostProcessor(DefaultTranslatorContext translatorContext, @Autowired(required = false) List<TranslatorFactoryPostProcessor> postProcessors) {
        TransLog transLog = TransManager.getTransLog();
        Optional.ofNullable(postProcessors)
                .ifPresent(param -> param.forEach(item -> {
                    translatorContext.addTranslatorFactoryPostProcessor(item);
                    transLog.debug("add translatorFactoryPostProcessor: {}", () -> item);
                }));
    }

}

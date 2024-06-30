package com.superkele.translation.boot.auto;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkele.translation.boot.config.properties.TranslationConfig;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.metadata.FieldTranslationFactory;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import com.superkele.translation.extension.serialize.jackson.JacksonWriteAspect;
import com.superkele.translation.extension.serialize.jackson.TranslationJsonNodeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = {"enable","json-serialize"}, havingValue = "true")
public class EasyTranslationJacksonSerializeAutoConfig {

    private final TranslationConfig config;

    private final DefaultTranslatorContext defaultTransExecutorContext;

    private final FieldTranslationFactory fieldTranslationFactory;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModules(new TranslationJsonNodeModule(fieldTranslationFactory, defaultTransExecutorContext,config));
    }

}

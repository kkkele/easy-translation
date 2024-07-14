package com.superkele.translation.boot.auto;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.superkele.translation.boot.config.properties.TranslationBootConfig;
import com.superkele.translation.core.context.support.DefaultTranslatorContext;
import com.superkele.translation.core.metadata.FieldTranslationInfoFactory;
import com.superkele.translation.extension.serialize.jackson.TranslationJsonNodeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@RequiredArgsConstructor
@AutoConfiguration
@ConditionalOnProperty(prefix = "easy-translation", name = {"enable","json-serialize"}, havingValue = "true")
public class EasyTranslationJacksonSerializeAutoConfig {

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModules(new TranslationJsonNodeModule());
    }

}

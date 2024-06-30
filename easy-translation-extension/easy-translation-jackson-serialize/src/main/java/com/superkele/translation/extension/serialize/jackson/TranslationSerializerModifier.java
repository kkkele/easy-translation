package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.superkele.translation.core.config.Config;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.mapping.support.DefaultTranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationFactory;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class TranslationSerializerModifier extends BeanSerializerModifier {
    private final FieldTranslationFactory filedTranslationFactory;
    private final TranslationInvoker translationInvoker;
    private final Config translationConfig;

    public TranslationSerializerModifier(FieldTranslationFactory filedTranslationFactory, TranslatorFactory translatorFactory, Config translationConfig) {
        this.filedTranslationFactory = filedTranslationFactory;
        this.translationInvoker = new DefaultTranslationInvoker(translatorFactory);
        this.translationConfig = translationConfig;
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        JavaType type = beanDesc.getType();
        Class<?> targetType = type.getRawClass();
        return Optional.ofNullable(getFieldTranslation(targetType))
                .map(fieldTranslation -> {
                    LogUtils.debug(log::debug, "type {} support json translation ", () -> targetType.getSimpleName());
                    return (JsonSerializer) new TranslationJsonSerializer(fieldTranslation, translationInvoker, serializer, translationConfig);
                })
                .orElse(super.modifySerializer(config, beanDesc, serializer));
    }


    protected FieldTranslation getFieldTranslation(Class<?> targetType) {
        return filedTranslationFactory.get(targetType, true);
    }

}

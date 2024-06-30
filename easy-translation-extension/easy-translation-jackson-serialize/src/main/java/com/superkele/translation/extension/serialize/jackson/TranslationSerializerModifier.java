package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.type.CollectionType;
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

    public TranslationSerializerModifier(FieldTranslationFactory filedTranslationFactory, TranslatorFactory translatorFactory) {
        this.filedTranslationFactory = filedTranslationFactory;
        this.translationInvoker = new DefaultTranslationInvoker(translatorFactory);
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        JavaType type = beanDesc.getType();
        Class<?> targetType = type.getRawClass();
        LogUtils.error(log::error, "serializer simple type begin analise {}", () -> targetType.getSimpleName());
        FieldTranslation fieldTranslation1 = getFieldTranslation(targetType);
        return Optional.ofNullable(fieldTranslation1)
                .map(fieldTranslation -> (JsonSerializer) new TranslationJsonSerializer(fieldTranslation, translationInvoker, serializer))
                .orElse(super.modifySerializer(config, beanDesc, serializer));
    }


    protected FieldTranslation getFieldTranslation(Class<?> targetType) {
        return filedTranslationFactory.get(targetType, true);
    }

}

package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.config.TranslationConfig;
import com.superkele.translation.core.context.FieldTranslationInfoContext;
import com.superkele.translation.core.log.TransLog;
import com.superkele.translation.core.mapping.TranslationInvoker;
import com.superkele.translation.core.mapping.support.DefaultTranslationInvoker;
import com.superkele.translation.core.metadata.FieldTranslationInfo;
import com.superkele.translation.core.metadata.FieldTranslationInfoFactory;
import com.superkele.translation.core.translator.factory.TranslatorFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class TranslationSerializerModifier extends BeanSerializerModifier {

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        JavaType type = beanDesc.getType();
        Class<?> targetType = type.getRawClass();
        return Optional.ofNullable(getFieldTranslation(targetType))
                .map(fieldTranslation -> {
                    TransLog transLog = TransManager.getTransLog();
                    transLog.debug("type {} support json translation ", () -> targetType.getSimpleName());
                    return (JsonSerializer) new TranslationJsonSerializer(fieldTranslation, serializer);
                })
                .orElse(super.modifySerializer(config, beanDesc, serializer));
    }


    protected FieldTranslationInfo getFieldTranslation(Class<?> targetType) {
        FieldTranslationInfoContext fieldTranslationInfoContext = TransManager.getFieldTranslationInfoContext();
        return fieldTranslationInfoContext.get(targetType, true);
    }

}

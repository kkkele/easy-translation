package com.superkele.translation.extension.serialize.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.support.MappingFiledTranslationBuilder;
import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class TranslationSerializerModifier extends BeanSerializerModifier {

    private final MappingFiledTranslationBuilder mappingFiledTranslationBuilder = new MappingFiledTranslationBuilder();

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            // 如果序列化器为 TranslationHandler 的话 将 Null 值也交给他处理
            if (writer.getSerializer() instanceof EasyTranslationJsonSerializer) {
                writer.assignNullSerializer(writer.getSerializer());
            }
        }
        return super.changeProperties(config, beanDesc, beanProperties);
    }

    @Override
    public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        Class<?> targetType = beanDesc.getType().getRawClass();
        LogUtils.debug(log::debug, "translationSerializerModify begin analise {}", () -> targetType.getSimpleName());
        return Optional.ofNullable(getFieldTranslation(targetType))
                .map(fieldTranslation -> (JsonSerializer) new EasyTranslationJsonSerializer(fieldTranslation))
                .orElse(super.modifySerializer(config, beanDesc, serializer));
    }


    protected FieldTranslation getFieldTranslation(Class<?> targetType) {
        return mappingFiledTranslationBuilder.build(targetType, true);
    }

}

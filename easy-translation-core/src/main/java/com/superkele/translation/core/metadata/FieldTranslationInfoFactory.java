package com.superkele.translation.core.metadata;

public interface FieldTranslationInfoFactory {

    FieldTranslationInfo get(Class<?> clazz, boolean isJsonSerialize);

}

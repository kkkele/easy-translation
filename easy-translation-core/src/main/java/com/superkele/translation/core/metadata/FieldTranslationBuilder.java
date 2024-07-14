package com.superkele.translation.core.metadata;

public interface FieldTranslationBuilder {

    FieldTranslationInfo build(Class<?> clazz, boolean isJsonSerialize);
}

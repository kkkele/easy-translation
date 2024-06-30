package com.superkele.translation.core.metadata;

public interface FieldTranslationBuilder {

    FieldTranslation build(Class<?> clazz,boolean isJsonSerialize);
}

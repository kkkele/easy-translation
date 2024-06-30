package com.superkele.translation.core.metadata;

public interface FieldTranslationRegistry {

    void register(Class<?> clazz,boolean isJsonSerialize, FieldTranslation fieldTranslation);

}

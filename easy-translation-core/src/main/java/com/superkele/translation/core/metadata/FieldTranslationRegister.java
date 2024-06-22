package com.superkele.translation.core.metadata;

public interface FieldTranslationRegister {

    void register(Class<?> clazz,boolean isJsonSerialize, FieldTranslation fieldTranslation);
}

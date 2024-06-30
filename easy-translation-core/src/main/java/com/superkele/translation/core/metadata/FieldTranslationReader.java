package com.superkele.translation.core.metadata;

public interface FieldTranslationReader {

    FieldTranslationRegistry getRegister();

    void load(String[] basePath);
}

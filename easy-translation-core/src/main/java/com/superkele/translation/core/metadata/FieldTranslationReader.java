package com.superkele.translation.core.metadata;

public interface FieldTranslationReader {

    FieldTranslationRegister getRegister();

    void load(String[] basePath, String[] excludePath);
}

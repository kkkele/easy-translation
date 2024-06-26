package com.superkele.translation.core.processor;

import com.superkele.translation.core.metadata.FieldTranslation;


public interface FieldTranslationHandler {

    FieldTranslation getFieldTranslation();

    void handle();

}

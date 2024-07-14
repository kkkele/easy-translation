package com.superkele.translation.core.processor;

import com.superkele.translation.core.metadata.FieldTranslationInfo;


public interface FieldTranslationHandler {

    FieldTranslationInfo getFieldTranslation();

    void handle();

}

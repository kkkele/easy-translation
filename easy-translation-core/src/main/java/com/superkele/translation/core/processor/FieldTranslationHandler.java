package com.superkele.translation.core.processor;

import com.superkele.translation.core.metadata.FieldTranslation;

import java.util.Collection;

public interface FieldTranslationHandler {

    FieldTranslation getFieldTranslation();

    void handle(Object obj);

    void handle(Collection collection,boolean async);
}

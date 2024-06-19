package com.superkele.translation.core.processor;

import com.superkele.translation.core.metadata.FieldTranslation;

import java.util.Collection;
import java.util.List;

public interface FieldTranslationHandler {

    FieldTranslation getFieldTranslation();

    void handle(boolean async);

}

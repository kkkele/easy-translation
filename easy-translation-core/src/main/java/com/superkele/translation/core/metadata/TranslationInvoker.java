package com.superkele.translation.core.metadata;

import java.util.Collection;

public interface TranslationInvoker {

    FieldTranslation getFieldTranslation();

    void translate(Object obj);

    void translateBatch(Collection collect);
}

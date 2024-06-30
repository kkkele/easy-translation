package com.superkele.translation.core.metadata;

import com.superkele.translation.core.translator.factory.TranslatorFactory;

public interface FieldTranslationFactory {


    FieldTranslation get(Class<?> clazz, boolean isJsonSerialize);


}

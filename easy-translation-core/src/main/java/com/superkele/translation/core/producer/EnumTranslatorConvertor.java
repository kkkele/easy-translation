package com.superkele.translation.core.producer;

import com.superkele.translation.core.metadata.Translator;

public interface EnumTranslatorConvertor extends TranslatorConverter {

    Translator convert(Class<? extends Enum> enumClazz, String mapperField, String mappedField);
}

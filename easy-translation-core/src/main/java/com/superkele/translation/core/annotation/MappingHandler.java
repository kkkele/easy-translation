package com.superkele.translation.core.annotation;

import com.superkele.translation.annotation.Mapping;

import java.lang.reflect.Field;

public interface MappingHandler {

    int MAX_TRANSLATOR_PARAM_LEN = 16;

    FieldTranslationInvoker convert(Field declaringField, Mapping mapping);
}

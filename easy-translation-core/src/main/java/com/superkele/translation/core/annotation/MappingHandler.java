package com.superkele.translation.core.annotation;

import com.superkele.translation.annotation.Mapping;

import java.lang.reflect.Field;

public interface MappingHandler {

    FieldTranslationInvoker convert(Field declaringField, Mapping mapping);
}

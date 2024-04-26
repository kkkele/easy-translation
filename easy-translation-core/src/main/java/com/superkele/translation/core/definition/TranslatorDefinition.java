package com.superkele.translation.core.definition;

import com.superkele.translation.core.enums.TranslatorType;
import com.superkele.translation.core.metadata.Translator;
import lombok.Data;

@Data
public class TranslatorDefinition {

    public static final String TRANSLATOR_CONDITION = "condition";
    public static final String TRANSLATOR_MAPPER = "mapper";
    public static final String TRANSLATOR_CONTEXT = "context";

    private Class<?> returnType;

    private Class<?>[] parameterTypes;

    private Class<? extends Translator> translatorClass;

    private Object invokeObj;

    private Translator translator;

    private int keyIndex;

    private int otherIndex;

    private TranslatorType type;
}

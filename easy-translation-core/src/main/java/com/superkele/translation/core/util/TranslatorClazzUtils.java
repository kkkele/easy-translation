package com.superkele.translation.core.util;

import com.superkele.translation.core.translator.ConditionTranslator;
import com.superkele.translation.core.translator.ContextTranslator;
import com.superkele.translation.core.translator.MapperTranslator;
import com.superkele.translation.core.translator.Translator;

public class TranslatorClazzUtils {

    public static Class<? extends Translator> getTranslatorName(int parameterLength) {
        return switch (parameterLength) {
            case 0 -> ContextTranslator.class;
            case 1 -> MapperTranslator.class;
            case 2 -> ConditionTranslator.class;
            default -> throw new IllegalStateException("Method parameterLength is too long");
        };
    }
}

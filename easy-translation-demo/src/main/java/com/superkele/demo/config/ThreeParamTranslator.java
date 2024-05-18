package com.superkele.demo.config;

import com.superkele.translation.core.translator.Translator;

public interface ThreeParamTranslator extends Translator {

    Object translate(Object var0, Object var1, Object var2);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2]);
    }
}

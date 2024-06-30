package com.superkele.translation.core.translator;

public interface ThreeParamTranslator extends Translator {

    Object translate(Object var1, Object var2, Object var3);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2]);
    }
}

package com.superkele.translation.core.translator;

public interface FourParamTranslator extends Translator{

    Object translate(Object arg1, Object arg2, Object arg3, Object arg4);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2], args[3]);
    }
}

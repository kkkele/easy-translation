package com.superkele.translation.core.translator;

public interface FiveParamTranslator extends Translator{

    Object translate(Object var1, Object var2, Object var3, Object var4, Object var5);

    @Override
    default Object doTranslate(Object... args) {
        return translate(args[0], args[1], args[2], args[3], args[4]);
    }
}

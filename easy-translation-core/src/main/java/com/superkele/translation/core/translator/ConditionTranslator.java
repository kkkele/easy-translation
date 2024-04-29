package com.superkele.translation.core.translator;


import com.superkele.translation.core.translator.handle.TranslateExecutor;

@FunctionalInterface
public interface ConditionTranslator extends Translator {

    Object translate(Object var0, Object var1);

    @Override
    default TranslateExecutor getDefaultTranslateHandler() {
        return args -> translate(args[0], args[1]);
    }
}

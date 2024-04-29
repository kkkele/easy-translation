package com.superkele.translation.core.translator;


import com.superkele.translation.core.translator.handle.TranslateExecutor;

@FunctionalInterface
public interface MapperTranslator extends Translator {

    Object translate(Object var0);

    @Override
    default TranslateExecutor getDefaultExecutor() {
        return args -> translate(args[0]);
    }
}

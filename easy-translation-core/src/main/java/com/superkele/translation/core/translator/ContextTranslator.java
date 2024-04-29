package com.superkele.translation.core.translator;

import com.superkele.translation.core.translator.handle.TranslateExecutor;

@FunctionalInterface
public interface ContextTranslator extends Translator {

    Object translate();

    @Override
    default TranslateExecutor getDefaultExecutor() {
        return args -> translate();
    }
}

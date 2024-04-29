package com.superkele.translation.core.translator;


import com.superkele.translation.core.translator.handle.TranslateExecutor;

public interface Translator {
    default TranslateExecutor getDefaultExecutor() {
        return null;
    }
}

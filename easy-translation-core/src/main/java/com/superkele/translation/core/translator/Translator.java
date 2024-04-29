package com.superkele.translation.core.translator;


import com.superkele.translation.core.translator.handle.TranslateHandler;

public interface Translator {
    default TranslateHandler getDefaultTranslateHandler() {
        return null;
    }
}

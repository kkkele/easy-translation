package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.handle.TranslateExecutor;

public interface TransExecutorFactory {

    TranslateExecutor findExecutor(String translator);

    boolean containsTranslator(String name);
}

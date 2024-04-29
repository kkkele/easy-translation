package com.superkele.translation.core.translator.factory;

import com.superkele.translation.core.translator.handle.TranslateExecutor;

public interface TransExecutorFactory {

    TranslateExecutor findExecutor(String translator);

    <T extends TranslateExecutor> T findExecutor(String name, Class<?> requireType);

    boolean containsTranslator(String name);
}

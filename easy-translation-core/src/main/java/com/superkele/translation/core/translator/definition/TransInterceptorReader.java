package com.superkele.translation.core.translator.definition;

public interface TransInterceptorReader {

    void loadTranslatorFactoryPostProcessors(String location);

    void loadTranslatorFactoryPostProcessors(String[] locations);

    void loadTranslatorPostProcessors(String location);

    void loadTranslatorPostProcessors(String[] locations);
}

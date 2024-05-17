package com.superkele.translation.core.translator.definition;

public interface TranslatorDefinitionReader {

    TranslatorDefinitionRegistry getRegistry();

    TranslatorLoader getTranslatorLoader();

    void loadDynamicTranslatorDefinitions(Object invokeObj);

    void loadDynamicTranslatorDefinitions(Object[] invokeObjs);

    void loadTranslatorDefinitions(String location);

    void loadTranslatorDefinitions(String[] locations);

}

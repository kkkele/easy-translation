package com.superkele.translation.core.translator.definition;

public interface TranslatorDefinitionReader {

    TranslatorDefinitionRegistry getRegistry();

    void loadVirtualTranslatorDefinitions(Object invokeObj);

    void loadVirtualTranslatorDefinitions(Object[] invokeObjs);

    void loadStaticTranslatorDefinitions(String location);

    void loadStaticTranslatorDefinitions(String[] locations);

    void loadEnumTranslatorDefinitions(String location);

    void loadEnumTranslatorDefinitions(String[] locations);
}

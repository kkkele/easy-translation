package com.superkele.translation.core.translator.definition;

import com.superkele.translation.core.translator.Resource;

public interface TranslatorDefinitionReader {

    void loadTranslatorDefinitions(String basePath);

    void loadTranslatorDefinitions(String[] basePath);

    void loadTranslatorDefinitions(Resource resource);
}

package com.superkele.translation.core.loader;

import com.superkele.translation.core.function.Translator;
import com.superkele.translation.core.util.Pair;

import java.util.List;

public interface TranslatorLoader {

    List<Pair<String, Translator>> load(String basePath);
}

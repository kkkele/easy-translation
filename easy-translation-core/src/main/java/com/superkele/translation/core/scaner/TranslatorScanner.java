package com.superkele.translation.core.scaner;

import com.superkele.translation.core.metadata.MethodInfo;

import java.util.List;

public interface TranslatorScanner {

    List<MethodInfo> scan(String... basePackages);
}

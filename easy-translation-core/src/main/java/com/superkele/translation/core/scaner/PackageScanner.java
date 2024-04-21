package com.superkele.translation.core.scaner;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.Translator;

import java.util.List;

public interface PackageScanner {

    String[] basePackages();

    List<Translator> scanTranslator();

    List<Mapping> scanMappings();
}

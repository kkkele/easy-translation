package com.superkele.translation.core.scaner;

import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.Translator;
import com.superkele.translation.core.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * <p>包扫描接口</p>
 */
public interface PackageScanner {

    String[] basePackages();

    List<Pair<Object,Method>> scanTranslator();

    List<Pair<Class<?>,Field>> scanMapping();
}

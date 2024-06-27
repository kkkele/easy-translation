package com.superkele.translation.boot.global;


import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class TranslationGlobalInformation {

    private static final List<String> TRANSLATOR_PACKAGES = new CopyOnWriteArrayList();
    private static final List<String> DOMAIN_PACKAGES = new CopyOnWriteArrayList();

    public static void addTranslatorPackage(Iterable<String> packages) {
        Optional.ofNullable(packages)
                .ifPresent(p -> {
                    for (String aPackage : p) {
                        TranslationGlobalInformation.TRANSLATOR_PACKAGES.add(aPackage);
                        LogUtils.debug(log::debug, " translator 增加包扫描:{}", () -> aPackage);
                    }
                });
    }


    public static void addDomainPackage(Iterable<String> packages) {
        Optional.ofNullable(packages)
                .ifPresent(p -> {
                    for (String aPackage : packages) {
                        TranslationGlobalInformation.DOMAIN_PACKAGES.add(aPackage);
                        LogUtils.debug(log::debug, " domain 增加包扫描:{}", () -> aPackage);
                    }
                });
    }

    public static List<String> getTranslatorPackages() {
        return TRANSLATOR_PACKAGES;
    }

    public static List<String> getDomainPackages() {
        return DOMAIN_PACKAGES;
    }
}

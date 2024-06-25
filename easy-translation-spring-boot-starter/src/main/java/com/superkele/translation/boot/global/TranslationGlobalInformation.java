package com.superkele.translation.boot.global;


import com.superkele.translation.core.util.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


@Slf4j
public class TranslationGlobalInformation {

    private static List<String> translatorPackages = new CopyOnWriteArrayList();
    private static List<String> domainPackages = new CopyOnWriteArrayList();

    public static void addTranslatorPackage(String... packages) {
        for (String aPackage : packages) {
            TranslationGlobalInformation.translatorPackages.add(aPackage);
            LogUtils.debug(log::debug," translator 增加包扫描:{}",()->aPackage);
        }
    }

    public static void addTranslatorPackage(Collection<String> packages) {
        Optional.ofNullable(packages)
                .ifPresent(p -> {
                    for (String aPackage : p) {
                        TranslationGlobalInformation.translatorPackages.add(aPackage);
                        LogUtils.debug(log::debug," translator 增加包扫描:{}",()->aPackage);
                    }
                });
    }


    public static void addDomainPackage(Collection<String> packages) {
        Optional.ofNullable(packages)
                .ifPresent(p -> {
                    for (String aPackage : packages) {
                        TranslationGlobalInformation.domainPackages.add(aPackage);
                        LogUtils.debug(log::debug," domain 增加包扫描:{}",()->aPackage);
                    }
                });
    }

    public static List<String> getTranslatorPackages() {
        return translatorPackages;
    }

    public static List<String> getDomainPackages() {
        return domainPackages;
    }
}

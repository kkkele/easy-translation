package com.superkele.translation.boot.global;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TranslationGlobalInformation {

    private static List<String> packages = new CopyOnWriteArrayList();

    public static void addPackage(String... packages) {
        for (String aPackage : packages) {
            TranslationGlobalInformation.packages.add(aPackage);
        }
    }

    public static List<String> getPackages() {
        return packages;
    }
}

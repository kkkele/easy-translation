package com.superkele.translation.boot.global;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class TranslationGlobalInformation {

    private static List<String> packages ;


    public static void addPackage(String... packages) {
        if (TranslationGlobalInformation.packages == null) {
            synchronized (TranslationGlobalInformation.class) {
                if (TranslationGlobalInformation.packages == null) {
                    TranslationGlobalInformation.packages = new CopyOnWriteArrayList();
                }
            }
        }
        for (String aPackage : packages) {
            TranslationGlobalInformation.packages.add(aPackage);
        }
    }

    public static List<String> getPackages() {
        return packages;
    }
}

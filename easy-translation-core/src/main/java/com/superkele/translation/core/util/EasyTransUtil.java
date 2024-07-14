package com.superkele.translation.core.util;


public class EasyTransUtil {

    private EasyTransUtil() {
    }

    public static void printEasyTranslation() {
        String str = "____  __  ____ _   _     _____  __   __   ____  ____ _     __  _____ _ ____ ____\r\n" +
                     "|___ [__] [___  \\_/   __   |   [__] [__]  |  |  [___ |    [__]   |   | |  | |  |\r\n" +
                     "|___ |  | ___]   |         |   |\\__ |  |  |  |  ___] |___ |  |   |   | |__| |  |\r\n" +
                     "("+TransConstants.VERSION+ ")\r\n" +
                     "docs:"+TransConstants.DOCS;
        System.out.println(str);
    }
}

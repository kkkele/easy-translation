package com.superkele.translation.core.util;

import java.io.IOException;
import java.util.Properties;

public class TransConstants {

    public static final String VERSION;
    public static final String DOCS = "https://kkkele.github.io/easy-translation-docs/#/";

    static {
        String version;
        Properties props = new Properties();
        try {
            props.load(TransConstants.class.getClassLoader().getResourceAsStream("application.properties"));
            version = props.getProperty("easy-translation.version");
        } catch (IOException e) {
            version = "unknown";
        }
        VERSION = version;
    }
}

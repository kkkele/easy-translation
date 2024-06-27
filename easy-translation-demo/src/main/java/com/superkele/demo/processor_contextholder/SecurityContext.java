package com.superkele.demo.processor_contextholder;

import com.superkele.translation.boot.annotation.Translator;

public class SecurityContext {

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    @Translator("getToken")
    public static String getToken() {
        return TOKEN.get();
    }

    public static void clear() {
        TOKEN.remove();
    }
}

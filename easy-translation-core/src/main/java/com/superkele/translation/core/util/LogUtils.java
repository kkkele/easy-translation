package com.superkele.translation.core.util;


import lombok.extern.slf4j.Slf4j;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class LogUtils {

    public static final String GREEN_COLOR = "\033[32m";
    public static final String RED_COLOR = "\033[31m";
    public static final String RESET_COLOR = "\033[0m";
    public static final String YELLOW_COLOR = "\033[33m";
    public static String PREFIX = "[Easy Translation] ";

    public static boolean printLog = false;

    public static void debug(BiConsumer<String,Object[]> printerAct, String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            printerAct.accept(GREEN_COLOR + PREFIX + logSupplier + RESET_COLOR, args);
        }
    }

    public static void info(BiConsumer<String,Object[]> printerAct,String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            printerAct.accept(PREFIX + logSupplier, args);
        }
    }

    public static void warn(BiConsumer<String,Object[]> printerAct,String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            printerAct.accept(YELLOW_COLOR + PREFIX + logSupplier + RESET_COLOR, args);
        }
    }

    public static void error(BiConsumer<String,Object[]> printerAct,String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            printerAct.accept(RED_COLOR + PREFIX + logSupplier + RESET_COLOR, args);
        }
    }
}

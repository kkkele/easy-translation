package com.superkele.translation.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class LogUtils {

    private static final String GREEN_COLOR = "\033[32m";
    private static final String RED_COLOR = "\033[31m";
    private static final String RESET_COLOR = "\033[0m";
    private static final String YELLOW_COLOR = "\033[33m";
    public static String PREFIX = "[Easy Translation] ";
    public static Logger logger = LoggerFactory.getLogger(LogUtils.class);
    public static boolean printLog = true;


    public static void debug(String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            logger.debug(GREEN_COLOR + PREFIX + logSupplier + RESET_COLOR, args);
        }
    }

    public static void info(String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            logger.info(PREFIX + logSupplier, args);
        }
    }

    public static void warn(String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            logger.warn(YELLOW_COLOR + PREFIX + logSupplier + RESET_COLOR, args);
        }
    }

    public static void error(String logSupplier, Supplier... params) {
        if (printLog) {
            Object[] args = new Object[params.length];
            for (int i = 0; i < params.length; i++) {
                args[i] = params[i].get();
            }
            logger.error(RED_COLOR + PREFIX + logSupplier + RESET_COLOR, args);
        }
    }
}

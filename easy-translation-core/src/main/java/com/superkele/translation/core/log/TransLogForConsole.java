package com.superkele.translation.core.log;

import cn.hutool.core.util.StrUtil;
import com.superkele.translation.core.TransManager;
import com.superkele.translation.core.config.TranslationConfig;

import java.util.function.Supplier;



public class TransLogForConsole implements TransLog {

    public static String TRACE_COLOR = "\033[0m";
    public static String DEBUG_COLOR = "\033[32m";
    public static String INFO_COLOR = "\033[34m";
    public static String WARN_COLOR = "\033[33m";
    public static String ERROR_COLOR = "\033[31m";
    public static String PREFIX = "[Easy Translation] ";

    public static String TRACE_PREFIX = "EasyTranslation [TRACE] :";
    public static String DEBUG_PREFIX = "EasyTranslation [DEBUG] :";
    public static String INFO_PREFIX = "EasyTranslation [INFO] :";
    public static String WARN_PREFIX = "EasyTranslation [WARN] :";
    public static String ERROR_PREFIX = "EasyTranslation [ERROR] :";

    public static String DEFAULT_COLOR = TRACE_COLOR;

    @Override
    public void trace(String str, Supplier<Object>... params) {
        printLog(LogLevel.TRACE, TRACE_COLOR, TRACE_PREFIX, str, params);
    }

    @Override
    public void debug(String str, Supplier<Object>... params) {
        printLog(LogLevel.DEBUG, DEBUG_COLOR, DEBUG_PREFIX, str, params);
    }

    @Override
    public void info(String str, Supplier<Object>... params) {
        printLog(LogLevel.INFO, INFO_COLOR, INFO_PREFIX, str, params);
    }

    @Override
    public void warn(String str, Supplier<Object>... params) {
        printLog(LogLevel.WARN, WARN_COLOR, WARN_PREFIX, str, params);
    }

    @Override
    public void error(String str, Supplier<Object>... params) {
        printLog(LogLevel.ERROR, ERROR_COLOR, ERROR_PREFIX, str, params);
    }


    /*    *
     * 打印日志到控制台
     * @param level 日志等级
     * @param color 颜色编码
     * @param prefix 前缀
     * @param str 字符串
     * @param args 参数列表*/
    public void printLog(LogLevel requiredLevel, String color, String prefix, String str, Supplier<Object>... params) {
        TranslationConfig translationConfig = TransManager.getConfig();
        if (translationConfig.isPrintLog()) {
            LogLevel logLevel = translationConfig.getLogLevel();
            if (logLevel.getLevel() <= requiredLevel.getLevel()) {
                if (translationConfig.isColorLog() == Boolean.TRUE) {
                    // 彩色日志
                    System.out.println(color + prefix + format(str, params) + DEFAULT_COLOR);
                } else {
                    System.out.println(prefix + format(str, params));
                }
            }
        }
    }

    public String format(String str, Supplier<Object>... params) {
        Object[] args = new Object[params.length];
        for (int i = 0; i < params.length; i++) {
            args[i] = params[i].get();
        }
        return StrUtil.format(str, args);
    }
}

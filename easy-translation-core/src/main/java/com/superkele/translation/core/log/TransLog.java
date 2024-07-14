package com.superkele.translation.core.log;

import java.util.function.Supplier;

public interface TransLog {

    /**
     * 输出 trace 日志
     * @param str 日志内容
     * @param params 参数列表
     */
    void trace(String str, Supplier<Object>... params);


    /**
     * 输出 debug 日志
     * @param str 日志内容
     * @param params 参数列表
     */
    void debug(String str, Supplier<Object>... params);

    /**
     * 输出 info 日志
     * @param str 日志内容
     * @param params 参数列表
     */
    void info(String str, Supplier<Object>... params);

    /**
     * 输出 warn 日志
     * @param str 日志内容
     * @param params 参数列表
     */
    void warn(String str, Supplier<Object>... params);

    /**
     * 输出 error 日志
     * @param str 日志内容
     * @param params 参数列表
     */
    void error(String str, Supplier<Object>... params);
}

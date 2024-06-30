package com.superkele.demo.processor_contextholder;

import com.superkele.translation.boot.annotation.Translator;

public class TimeRecorder {

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    public static void set(long startTime) {
        START_TIME.set(startTime);
    }

    @Translator("getStartTime")
    public static long get() {
        return START_TIME.get();
    }

    //clear
    public static void clear() {
        START_TIME.remove();
    }
}

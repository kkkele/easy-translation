package com.superkele.translation.test.util;

public class TimeRecorder {

    public static boolean isDebug = false;

    public static long record(Runnable runnable, int repeatTime) {
        long start = System.currentTimeMillis();
        int i = 0;
        while (i < repeatTime) {
            runnable.run();
            i++;
        }
        return System.currentTimeMillis() - start;
    }

    public static void printTime(String name, Runnable runnable) {
        long record = record(runnable, 1);
        if (isDebug)
            System.out.println(name + "=> cost: " + record + "ms");
    }
}

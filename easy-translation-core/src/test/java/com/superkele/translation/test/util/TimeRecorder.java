package com.superkele.translation.test.util;

public class TimeRecorder {

    public static long record(Runnable runnable,int repeatTime){
        long start = System.currentTimeMillis();
        int i = 0;
        while (i < repeatTime){
            runnable.run();
            i++;
        }
        return System.currentTimeMillis() - start;
    }
}

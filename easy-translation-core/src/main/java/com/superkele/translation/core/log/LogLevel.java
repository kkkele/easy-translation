package com.superkele.translation.core.log;

public enum LogLevel {
    TRACE(0),

    DEBUG(1),

    INFO(2),

    WARN(3),

    ERROR(4);

    private final int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}

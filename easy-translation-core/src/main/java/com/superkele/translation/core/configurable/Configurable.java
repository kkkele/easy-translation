package com.superkele.translation.core.configurable;

import java.util.function.Consumer;

public interface Configurable<T> {

    /**
     * 刷新
     */
    void refresh();

    void notice();

    void addListener(Consumer<T> action);
}

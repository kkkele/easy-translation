package com.superkele.translation.core.thread;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContextHolder {

    /**
     * 用来提供上下文
     */
    private Supplier<Object> supplier;

    /**
     * 用来消费上下文对象
     */
    private Consumer<Object> consumer;

    public Supplier<Object> getSupplier() {
        return supplier;
    }

    public ContextHolder setSupplier(Supplier<Object> supplier) {
        this.supplier = supplier;
        return this;
    }

    public Consumer<Object> getConsumer() {
        return consumer;
    }

    public ContextHolder setConsumer(Consumer<Object> consumer) {
        this.consumer = consumer;
        return this;
    }
}

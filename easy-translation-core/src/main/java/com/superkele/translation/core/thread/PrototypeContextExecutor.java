package com.superkele.translation.core.thread;

public class PrototypeContextExecutor {

    private Object context;

    private final ContextHolder contextHolder;

    public PrototypeContextExecutor(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
    }

    public void setPassValue() {
        context = contextHolder.getSupplier().get();
    }

    public void passContext() {
        contextHolder.getConsumer().accept(context);
    }
}

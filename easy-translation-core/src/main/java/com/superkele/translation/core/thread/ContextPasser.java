package com.superkele.translation.core.thread;

public class ContextPasser {

    private final ContextHolder contextHolder;
    private Object context;
    private Thread mainThread;

    public ContextPasser(ContextHolder contextHolder) {
        this.contextHolder = contextHolder;
        this.mainThread = Thread.currentThread();
        init();
    }

    private void init() {
        this.context = contextHolder.getContext();
    }

    public void passContext() {
        contextHolder.passContext(context);
    }

    public void clearContext() {
        if (Thread.currentThread() != mainThread) {
            contextHolder.clearContext();
        }
    }
}

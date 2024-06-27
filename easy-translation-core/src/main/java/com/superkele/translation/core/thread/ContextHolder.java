package com.superkele.translation.core.thread;

public interface ContextHolder<T> {

    T getContext();

    void passContext(T context);

    void clearContext();
}

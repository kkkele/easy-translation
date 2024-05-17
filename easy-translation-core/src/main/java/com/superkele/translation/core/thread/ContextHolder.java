package com.superkele.translation.core.thread;

public interface ContextHolder {

    Object getContext();

    void passContext(Object context);

    void clearContext();
}

package com.superkele.translation.extension.executecallback;

@FunctionalInterface
public interface TranslateExecuteCallBack<T> {

    void onSuccess(T result);
}

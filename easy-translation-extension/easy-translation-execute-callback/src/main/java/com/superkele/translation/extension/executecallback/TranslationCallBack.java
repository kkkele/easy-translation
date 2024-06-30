package com.superkele.translation.extension.executecallback;

public interface TranslationCallBack<T> {

    /**
     * 匹配翻译器名称，当匹配到时，为该翻译器执行增加回调
     *
     * @return 正则表达式
     */
    String match();

    /**
     * 翻译成功回调
     * @param result 翻译结果
     */
    void onSuccess(T result);
}

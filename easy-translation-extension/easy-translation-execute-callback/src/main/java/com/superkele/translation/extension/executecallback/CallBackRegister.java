package com.superkele.translation.extension.executecallback;

public interface CallBackRegister<T> {

    /**
     * 匹配翻译器名称，当匹配到时，为该翻译器执行增加回调
     *
     * @return 正则表达式
     */
    String match();

    TranslateExecuteCallBack<T> callBack();

    default int sort(){
        return 0;
    }
}

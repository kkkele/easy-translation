package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.NullPointerExceptionHandler;

public class DefaultNullPointerExceptionHandler implements NullPointerExceptionHandler {
    @Override
    public void handle(NullPointerException exception) {
        System.err.println("EasyTrans 获取值时引发了空指针异常");
        throw exception;
    }
}

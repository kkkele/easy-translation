package com.superkele.translation.annotation.constant;

import com.superkele.translation.annotation.NullPointerExceptionHandler;

public class IgnoreNullPointerExceptionHandler implements NullPointerExceptionHandler {
    @Override
    public void handle(NullPointerException exception) {
        return;
    }
}

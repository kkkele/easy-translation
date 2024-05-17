package com.superkele.translation.annotation;

public interface NullPointerExceptionHandler {

    default void handle(NullPointerException exception){
        throw exception;
    }
}

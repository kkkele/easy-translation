package com.superkele.translation.core.exception;

public class NotDefineException extends RuntimeException{

    public NotDefineException() {
        super("not define the item");
    }

    public NotDefineException(String message) {
        super(message);
    }
}

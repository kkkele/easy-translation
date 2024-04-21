package com.superkele.translation.core.exception;

public class AssertException extends RuntimeException{


    public AssertException() {
        super("assert error");
    }

    public AssertException(String message) {
        super(message);
    }

    public AssertException(String message, Throwable cause) {
        super(message, cause);
    }
}

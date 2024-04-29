package com.superkele.translation.core.exception;

public class TranslationException extends RuntimeException{
    public TranslationException() {
    }

    public TranslationException(String message) {
        super(message);
    }

    public TranslationException(String message, Throwable cause) {
        super(message, cause);
    }
}

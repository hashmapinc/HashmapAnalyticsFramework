package com.hashmap.haf.metadata.config.exceptions;

public class IncorrectParameterException extends RuntimeException {

    private static final long serialVersionUID = -2694243827026730022L;

    public IncorrectParameterException(String message) {
        super(message);
    }

    public IncorrectParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.hashmap.haf.metadata.config.exceptions;

public class DataValidationException extends RuntimeException {


    private static final long serialVersionUID = 1964788504460244477L;

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

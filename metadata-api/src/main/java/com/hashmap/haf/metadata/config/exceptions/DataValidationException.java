package com.hashmap.haf.metadata.config.exceptions;

public class DataValidationException extends RuntimeException {

//    private static final long serialVersionUID = 601995650578985289L;

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

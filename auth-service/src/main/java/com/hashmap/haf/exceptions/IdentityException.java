package com.hashmap.haf.exceptions;

public class IdentityException extends Exception {

    private static final long serialVersionUID = 1L;

    private IdentityErrorCode errorCode;

    public IdentityException() {
        super();
    }

    public IdentityException(IdentityErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public IdentityException(String message, IdentityErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public IdentityException(String message, Throwable cause, IdentityErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public IdentityException(Throwable cause, IdentityErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public IdentityErrorCode getErrorCode() {
        return errorCode;
    }

}

package com.hashmap.haf.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Date;

public class IdentityErrorResponse {
    // HTTP Response Status Code
    private final HttpStatus status;

    // General Error message
    private final String message;

    // Error code
    private final IdentityErrorCode errorCode;

    private final Date timestamp;

    protected IdentityErrorResponse(final String message, final IdentityErrorCode errorCode, HttpStatus status) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.timestamp = new java.util.Date();
    }

    public static IdentityErrorResponse of(final String message, final IdentityErrorCode errorCode, HttpStatus status) {
        return new IdentityErrorResponse(message, errorCode, status);
    }

    public Integer getStatus() {
        return status.value();
    }

    public String getMessage() {
        return message;
    }

    public IdentityErrorCode getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

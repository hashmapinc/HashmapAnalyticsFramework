package com.hashmapinc.haf.exceptions;

import org.springframework.boot.ExitCodeGenerator;

public class SchemaCreationException extends RuntimeException implements ExitCodeGenerator {

    public SchemaCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}

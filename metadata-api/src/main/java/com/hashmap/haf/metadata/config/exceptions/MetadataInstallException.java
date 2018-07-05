package com.hashmap.haf.metadata.config.exceptions;

import org.springframework.boot.ExitCodeGenerator;

public class MetadataInstallException extends RuntimeException implements ExitCodeGenerator {

    public MetadataInstallException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getExitCode() {
        return 1;
    }

}

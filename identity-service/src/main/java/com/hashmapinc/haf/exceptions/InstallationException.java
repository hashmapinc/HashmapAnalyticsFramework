package com.hashmapinc.haf.exceptions;

import org.springframework.boot.ExitCodeGenerator;

public class InstallationException extends RuntimeException implements ExitCodeGenerator {

    public InstallationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getExitCode() {
        return 1;
    }
}

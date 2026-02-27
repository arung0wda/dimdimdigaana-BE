package com.arp.dimdimdigaana.exception;

import lombok.Getter;

/**
 * Base runtime exception for the entire application.
 * <p>
 * Every custom exception extends this, carrying an {@link ErrorCode} that the
 * global exception handler uses to build the HTTP response.
 * <p>
 * All constructors accept an optional {@code cause} so context is never lost —
 * the full stacktrace is logged exactly once inside the global handler.
 */
@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDefaultMessage(), cause);
        this.errorCode = errorCode;
    }
}


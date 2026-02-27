package com.arp.dimdimdigaana.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Centralised error codes. Every custom exception carries one of these.
 * Add new entries here as the application grows.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ── User domain ─────────────────────────────────────────────
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "Username already exists", HttpStatus.CONFLICT),

    // ── Generic / catch-all ─────────────────────────────────────
    BAD_REQUEST("BAD_REQUEST", "Bad request", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("INTERNAL_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}


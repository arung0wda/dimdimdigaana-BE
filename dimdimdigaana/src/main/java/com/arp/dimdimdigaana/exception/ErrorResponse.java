package com.arp.dimdimdigaana.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Standardised error response returned by every error-handling path.
 * <p>
 * Serialised to JSON by Spring automatically.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** ISO-8601 UTC timestamp of the error */
    private final String timestamp;

    /** Machine-readable error code (e.g. USER_NOT_FOUND) */
    private final String code;

    /** Human-readable message safe to show to clients */
    private final String message;

    /** HTTP status value (e.g. 404) */
    private final int status;

    /** Request path that triggered the error (optional) */
    private final String path;
}


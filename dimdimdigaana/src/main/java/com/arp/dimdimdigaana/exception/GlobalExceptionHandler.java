package com.arp.dimdimdigaana.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

/**
 * Single, global exception handler for the entire application.
 * <p>
 * <b>Logging strategy:</b> every exception's full stacktrace is logged exactly
 * once — here. Service / controller layers should only use {@code log.warn} or
 * {@code log.info} for <em>business</em> context (never the stacktrace). If
 * lower layers need to add context, they wrap the original exception as the
 * {@code cause} of the thrown {@link AppException}.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Application-level exceptions ────────────────────────────

    /**
     * Handles every {@link AppException} (and its subclasses).
     * The {@link ErrorCode} carried by the exception drives the HTTP status
     * and the error code in the response body.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex,
                                                            HttpServletRequest request) {
        ErrorCode errorCode = ex.getErrorCode();
        HttpStatus status = errorCode.getHttpStatus();

        if (status.is5xxServerError()) {
            log.error("[{}] {} — path: {}", errorCode.getCode(), ex.getMessage(),
                    request.getRequestURI(), ex);
        } else {
            log.warn("[{}] {} — path: {}", errorCode.getCode(), ex.getMessage(),
                    request.getRequestURI());
        }

        return buildResponse(errorCode.getCode(), ex.getMessage(), status, request);
    }

    // ── Spring / framework exceptions ───────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");

        log.warn("[BAD_REQUEST] Validation failed — path: {} — details: {}",
                request.getRequestURI(), message);

        return buildResponse("BAD_REQUEST", message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        log.warn("[BAD_REQUEST] Malformed request body — path: {}", request.getRequestURI());
        return buildResponse("BAD_REQUEST", "Malformed request body",
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        String message = "Invalid value for parameter '" + ex.getName() + "'";
        log.warn("[BAD_REQUEST] {} — path: {}", message, request.getRequestURI());
        return buildResponse("BAD_REQUEST", message, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest request) {
        log.warn("[BAD_REQUEST] {} — path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse("BAD_REQUEST", ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest request) {
        log.warn("[METHOD_NOT_ALLOWED] {} — path: {}", ex.getMessage(), request.getRequestURI());
        return buildResponse("METHOD_NOT_ALLOWED", ex.getMessage(),
                HttpStatus.METHOD_NOT_ALLOWED, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex,
                                                               HttpServletRequest request) {
        log.warn("[NOT_FOUND] No endpoint found — path: {}", request.getRequestURI());
        return buildResponse("NOT_FOUND", "No endpoint found for this path",
                HttpStatus.NOT_FOUND, request);
    }

    // ── Catch-all for truly unexpected errors ───────────────────

    /**
     * Last resort. Logs the full stacktrace at ERROR level so ops can
     * investigate, but never leaks internal details to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex,
                                                          HttpServletRequest request) {
        log.error("[INTERNAL_ERROR] Unexpected exception — path: {}",
                request.getRequestURI(), ex);

        return buildResponse("INTERNAL_ERROR", "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ── Helper ──────────────────────────────────────────────────

    private ResponseEntity<ErrorResponse> buildResponse(String code,
                                                        String message,
                                                        HttpStatus status,
                                                        HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .code(code)
                .message(message)
                .status(status.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}


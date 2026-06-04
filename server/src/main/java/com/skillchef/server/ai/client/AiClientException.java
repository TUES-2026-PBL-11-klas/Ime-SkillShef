package com.skillchef.server.ai.client;

import java.time.Duration;

/**
 * Raised when an AI provider call fails. {@link #isRetryable()} distinguishes
 * transient failures (HTTP 429 / 5xx / network timeout) that a retry may
 * resolve from permanent ones (bad request, missing key, safety block).
 */
public class AiClientException extends RuntimeException {

    private final boolean retryable;
    private final Integer statusCode;
    private final Duration retryAfter;

    public AiClientException(String message, boolean retryable, Integer statusCode,
                             Duration retryAfter, Throwable cause) {
        super(message, cause);
        this.retryable = retryable;
        this.statusCode = statusCode;
        this.retryAfter = retryAfter;
    }

    public static AiClientException retryable(Integer statusCode, String message, Duration retryAfter) {
        return new AiClientException(message, true, statusCode, retryAfter, null);
    }

    public static AiClientException retryable(Integer statusCode, String message,
                                              Duration retryAfter, Throwable cause) {
        return new AiClientException(message, true, statusCode, retryAfter, cause);
    }

    public static AiClientException nonRetryable(Integer statusCode, String message) {
        return new AiClientException(message, false, statusCode, null, null);
    }

    public static AiClientException nonRetryable(Integer statusCode, String message, Throwable cause) {
        return new AiClientException(message, false, statusCode, null, cause);
    }

    public boolean isRetryable() {
        return retryable;
    }

    /** Provider HTTP status, if the failure originated from an HTTP response. */
    public Integer getStatusCode() {
        return statusCode;
    }

    /** Server-suggested wait from a {@code Retry-After} header, if present. */
    public Duration getRetryAfter() {
        return retryAfter;
    }
}

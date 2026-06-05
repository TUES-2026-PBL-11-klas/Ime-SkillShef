package com.skillchef.server.ai;

import org.springframework.http.HttpStatus;

/** Domain-level failure for the AI assistant feature, carrying the HTTP status to surface to the client. */
public class AiApiException extends RuntimeException {

    private final HttpStatus status;

    public AiApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AiApiException notFound(String message) {
        return new AiApiException(HttpStatus.NOT_FOUND, message);
    }

    public static AiApiException forbidden(String message) {
        return new AiApiException(HttpStatus.FORBIDDEN, message);
    }
}

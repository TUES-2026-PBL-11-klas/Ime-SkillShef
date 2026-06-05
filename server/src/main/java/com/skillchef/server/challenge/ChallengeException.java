package com.skillchef.server.challenge;

import org.springframework.http.HttpStatus;

/** Domain-level failure for the challenges feature, carrying the HTTP status to surface to the client. */
public class ChallengeException extends RuntimeException {

    private final HttpStatus status;

    public ChallengeException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ChallengeException notFound(String message) {
        return new ChallengeException(HttpStatus.NOT_FOUND, message);
    }

    public static ChallengeException badRequest(String message) {
        return new ChallengeException(HttpStatus.BAD_REQUEST, message);
    }
}

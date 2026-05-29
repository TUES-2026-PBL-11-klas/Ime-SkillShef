package com.skillchef.server.auth;

import org.springframework.http.HttpStatus;

/** Domain-level auth failure carrying the HTTP status to surface to the client. */
public class AuthException extends RuntimeException {

    private final HttpStatus status;

    public AuthException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static AuthException conflict(String message) {
        return new AuthException(HttpStatus.CONFLICT, message);
    }

    public static AuthException unauthorized(String message) {
        return new AuthException(HttpStatus.UNAUTHORIZED, message);
    }

    public static AuthException badRequest(String message) {
        return new AuthException(HttpStatus.BAD_REQUEST, message);
    }
}

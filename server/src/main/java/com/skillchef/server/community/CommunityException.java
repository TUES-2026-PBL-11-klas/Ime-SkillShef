package com.skillchef.server.community;

import org.springframework.http.HttpStatus;

/** Domain-level failure for the community feature, carrying the HTTP status to surface to the client. */
public class CommunityException extends RuntimeException {

    private final HttpStatus status;

    public CommunityException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static CommunityException notFound(String message) {
        return new CommunityException(HttpStatus.NOT_FOUND, message);
    }

    public static CommunityException forbidden(String message) {
        return new CommunityException(HttpStatus.FORBIDDEN, message);
    }

    public static CommunityException badRequest(String message) {
        return new CommunityException(HttpStatus.BAD_REQUEST, message);
    }

    public static CommunityException conflict(String message) {
        return new CommunityException(HttpStatus.CONFLICT, message);
    }
}

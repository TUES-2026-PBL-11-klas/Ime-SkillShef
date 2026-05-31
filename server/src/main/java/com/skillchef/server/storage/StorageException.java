package com.skillchef.server.storage;

import org.springframework.http.HttpStatus;

/** Storage-layer failure carrying the HTTP status to surface to the client. */
public class StorageException extends RuntimeException {

    private final HttpStatus status;

    public StorageException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static StorageException badRequest(String message) {
        return new StorageException(HttpStatus.BAD_REQUEST, message);
    }

    public static StorageException forbidden(String message) {
        return new StorageException(HttpStatus.FORBIDDEN, message);
    }

    public static StorageException notFound(String message) {
        return new StorageException(HttpStatus.NOT_FOUND, message);
    }

    public static StorageException internal(String message) {
        return new StorageException(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}

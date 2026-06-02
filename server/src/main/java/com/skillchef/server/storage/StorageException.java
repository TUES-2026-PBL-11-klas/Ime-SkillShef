package com.skillchef.server.storage;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Raised when an upload cannot be validated or persisted. */
public class StorageException extends ResponseStatusException {

    public StorageException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public static StorageException badRequest(String reason) {
        return new StorageException(HttpStatus.BAD_REQUEST, reason);
    }

    public static StorageException internal(String reason) {
        return new StorageException(HttpStatus.INTERNAL_SERVER_ERROR, reason);
    }
}

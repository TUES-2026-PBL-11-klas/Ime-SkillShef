package com.skillchef.server.challenge;

import com.skillchef.server.storage.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = ChallengeController.class)
public class ChallengeExceptionHandler {

    @ExceptionHandler(ChallengeException.class)
    public ResponseEntity<Map<String, Object>> handleChallenge(ChallengeException ex) {
        return ResponseEntity.status(ex.getStatus()).body(body(ex.getStatus(), ex.getMessage()));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<Map<String, Object>> handleStorage(StorageException ex) {
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleTooLarge(MaxUploadSizeExceededException ex) {
        HttpStatus status = HttpStatus.PAYLOAD_TOO_LARGE;
        return ResponseEntity.status(status).body(body(status, "The uploaded file is too large"));
    }

    /** {@code error} carries the human-readable message surfaced by the frontend transport. */
    private static Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        return body;
    }
}

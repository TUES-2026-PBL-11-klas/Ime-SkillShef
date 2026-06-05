package com.skillchef.server.ai;

import com.skillchef.server.ai.client.AiClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = AiController.class)
public class AiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(AiExceptionHandler.class);

    @ExceptionHandler(AiApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(AiApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(body(ex.getStatus(), ex.getMessage()));
    }

    /** AI provider failures (unconfigured key, rate limit, upstream error) — surfaced as 503. */
    @ExceptionHandler(AiClientException.class)
    public ResponseEntity<Map<String, Object>> handleClient(AiClientException ex) {
        log.warn("AI provider call failed (status={}): {}", ex.getStatusCode(), ex.getMessage());
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status)
                .body(body(status, "The AI assistant is temporarily unavailable. Please try again in a moment."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(body(HttpStatus.BAD_REQUEST, message.isBlank() ? "Validation failed" : message));
    }

    /**
     * {@code error} carries the human-readable message (the frontend's shared
     * transport surfaces this field to the UI).
     */
    private static Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", message);
        return body;
    }
}

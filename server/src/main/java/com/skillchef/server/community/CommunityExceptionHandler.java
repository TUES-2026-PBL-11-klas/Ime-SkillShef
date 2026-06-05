package com.skillchef.server.community;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {RecipePostController.class, EngagementController.class, FeedController.class})
public class CommunityExceptionHandler {

    @ExceptionHandler(CommunityException.class)
    public ResponseEntity<Map<String, Object>> handleCommunity(CommunityException ex) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(HttpStatusCode.valueOf(status.value())).body(body(status, ex.getMessage()));
    }

    @ExceptionHandler(FeedController.FeedAuthException.class)
    public ResponseEntity<Map<String, Object>> handleFeedAuth(FeedController.FeedAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST,
                message.isBlank() ? "Validation failed" : message));
    }

    private static Map<String, Object> body(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}

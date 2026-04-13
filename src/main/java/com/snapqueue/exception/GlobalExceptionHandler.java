package com.snapqueue.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

// catches exceptions thrown anywhere in the app and converts them to clean JSON responses
// without this, Spring would return ugly HTML error pages or stack traces to the client
// @RestControllerAdvice means it applies to all @RestController classes automatically
@RestControllerAdvice
public class GlobalExceptionHandler {

    // handles our custom 404 — thrown when findById or findByEmail returns empty
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status", 404, "message", ex.getMessage()));
    }

    // handles ResponseStatusException — we throw these in FeedbackService for 403 and 400 cases
    // e.g. "only managers can approve" or "only PENDING feedback can be approved"
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of("status", ex.getStatusCode().value(), "message", ex.getReason()));
    }

    // catch-all for anything else — mostly for the duplicate email/ID errors from registration
    // returns 400 since those are bad request situations
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("status", 400, "message", ex.getMessage()));
    }
}

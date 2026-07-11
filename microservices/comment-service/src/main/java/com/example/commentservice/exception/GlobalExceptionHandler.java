package com.example.commentservice.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        Map<String, Object> body = baseBody(HttpStatus.NOT_FOUND, "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Bad Request");
        body.put("fields", errors);
        return ResponseEntity.badRequest().body(body);
    }

    // post-service is down or unreachable -> we cannot verify the post.
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, Object>> handleDownstream(RestClientException ex) {
        Map<String, Object> body = baseBody(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable");
        body.put("message", "post-service is unavailable, cannot verify the post right now");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    private Map<String, Object> baseBody(HttpStatus status, String error) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        return body;
    }
}

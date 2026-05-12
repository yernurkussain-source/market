package kz.project.mymarket.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseConflict(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Database constraint error. Please check related records.", null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, Object fields) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", message);
        if (fields != null) response.put("fields", fields);
        return ResponseEntity.status(status).body(response);
    }
}

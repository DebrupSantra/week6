package com.example.blog.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(
            ConflictException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request, Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> validationErrors = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                validationErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Request validation failed", request, validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException exception, HttpServletRequest request) {
        Map<String, String> validationErrors = new TreeMap<>();
        exception.getConstraintViolations().forEach(violation ->
                validationErrors.put(violation.getPropertyPath().toString(), violation.getMessage()));
        return error(HttpStatus.BAD_REQUEST, "Request validation failed", request, validationErrors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableRequest(
            HttpMessageNotReadableException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "Request body is missing or malformed", request, Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException exception, HttpServletRequest request) {
        logger.warn("Request violated a data integrity constraint at {}", request.getRequestURI());
        return error(HttpStatus.CONFLICT, "The request conflicts with existing data", request, Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(
            Exception exception, HttpServletRequest request) {
        logger.error("Unexpected error handling {}", request.getRequestURI(), exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request, Map.of());
    }

    private ResponseEntity<ApiError> error(
            HttpStatus status, String message, HttpServletRequest request, Map<String, String> validationErrors) {
        ApiError body = new ApiError(
                Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), validationErrors);
        return ResponseEntity.status(status).body(body);
    }
}

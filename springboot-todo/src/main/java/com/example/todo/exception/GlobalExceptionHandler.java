package com.example.todo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST controllers
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    //logger
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handle missing request header exceptions
    // This will catch MissingRequestHeaderException thrown when a required header is missing.
    // This is a built-in Spring exception.
    // This mechanism is used to handle when a single header is missing. It cannot handle multiple missing headers at once.
    // So if your requirement is to report all missing headers at once, use the IllegalArgumentException mechanism below.
    /* Example JSON response:
      {
	    "code": "MISSING_HEADER",
	    "message": "Missing required header: X-Client-Id",
	    "status": 400,
	    "timestamp": "2025-09-11T15:20:11.381627"
      }
    */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingHeader(MissingRequestHeaderException ex) {
        // Log the exception details
        // log that a required header is missing
        logger.error("MissingRequestHeaderException: {}", ex.getMessage());
        String message = "Missing required header: " + ex.getHeaderName();
        ApiErrorResponse error = new ApiErrorResponse("MISSING_HEADER", message, HttpStatus.BAD_REQUEST.value(), java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // Handle IllegalArgumentException for other validation errors
    // This will catch IllegalArgumentException thrown for other validation issues.
    // One of the places we throw this is in TodoController when required headers are missing.
    // This mechanism is used to handle when multiple headers are missing, and we want to report all at once.
    /* Example JSON response:
      {
        "code": "INVALID_ARGUMENT",
        "message": "Missing required headers: X-Client-Id X-Request-Id",
        "status": 400,
        "timestamp": "2025-09-11T15:22:45.123456"
      }
    */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        // Log the exception details
        logger.error("Handling IllegalArgumentException in GlobalExceptionHandler: {}", ex.getMessage());
        logger.error("IllegalArgumentException: {}", ex.getMessage());
        // Create a custom error response
        ApiErrorResponse error = new ApiErrorResponse("INVALID_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST.value(), java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /** Handle ResourceNotFoundException
     * This will catch ResourceNotFoundException thrown when a requested resource is not found.
     * This is a custom exception defined in the application.
     * Example usage: In a GET endpoint, if the requested item by ID does not exist, throw this exception.
     * This will result in a 404 Not Found response.
     *
     * Example JSON response:
      {
        "code": "NOT_FOUND",
        "message": "Resource with ID 123 not found",
        "status": 404,
        "timestamp": "2025-09-11T15:25:30.654321"
      }
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        // Log the exception details
        logger.error("Handling ResourceNotFoundException in GlobalExceptionHandler: {}", ex.getMessage());
        logger.error("ResourceNotFoundException: {}", ex.getMessage());
        // Create a custom error response
        ApiErrorResponse error = new ApiErrorResponse("NOT_FOUND", ex.getMessage(), HttpStatus.NOT_FOUND.value(), java.time.LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}

package com.example.winttodo.exception;

/**
 * GlobalExceptionHandler class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/

import com.example.winttodo.dto.ApiErrorResponse;
import com.example.winttodo.utils.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

/**
 * Handles validation and other exceptions globally, ensuring meaningful error JSON is returned.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Logger can be added here for better traceability
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // This exception handler catches validation errors thrown when @Valid fails on the request body
    // This collects all field errors and global errors, formats them, and returns a structured error response
    // with a 400 Bad Request status.
    // This improves client usability by providing clear feedback on what went wrong.
    // Always log the exception details for debugging.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Log the exception details
//        logger.info("Entering {}.{}",
//                this.getClass().getSimpleName(),
//                Thread.currentThread().getStackTrace()[1].getMethodName()
//        );
        LogUtils.logMethodEntry(logger, this);
        logger.error(ex.getMessage(), ex);

        // For each error, build a readable message like: "Field 'title': Title is mandatory"
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format(
                        "Field '%s': %s (rejected value: %s)",
                        fieldError.getField(),
                        fieldError.getDefaultMessage(),
                        fieldError.getRejectedValue()))
                .collect(toList());

        // Optionally, get global errors (not tied to fields)
        List<String> globalErrors = ex.getBindingResult().getGlobalErrors().stream()
                .map(objectError -> String.format(
                        "Object '%s': %s",
                        objectError.getObjectName(),
                        objectError.getDefaultMessage()))
                .toList();

        errors.addAll(globalErrors);

        /*
        Example api response when title is empty and dueDate is in the past:
        {
	        "code": "VALIDATION_FAILED",
	        "message": "Validation failed for: TodoRequest",
	        "status": 400,
	        "timestamp": "2025-09-19T10:51:03.760628",
	        "errors": [
		        "Field 'title': Title is mandatory (rejected value: null)",
		        "Field 'dueDate': Due date must not be in the past (rejected value: 2024-06-11)"
	        ],
	        "path": "/wint/api/todos"
        }
         */

        /* TLDR;
        `.collect(toList())` is the classic way to collect a Java Stream into a `List` using the `Collectors.toList()` collector.
        `toList()` (without `Collectors`) is a newer, simpler terminal operation introduced in Java 16 that directly collects a Stream into a `List`.

        **In this code:**
        - `.collect(toList())` requires a static import of `Collectors.toList()`.
        - `.toList()` is a method on the Stream itself, no import needed.

        Both produce a `List`, but `.toList()` is more concise and preferred in modern Java (16+).
        Functionally, in your method, they do the same thing.
         */

        // Create a summary message
        String summaryMsg = "Validation failed for: " +
                Objects.requireNonNull(ex.getBindingResult().getTarget()).getClass().getSimpleName();

        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "VALIDATION_FAILED",
                summaryMsg,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                errors,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle MissingRequestHeaderException
    // This is thrown when a required header is missing
    // We catch it here to return a 400 Bad Request with details
    // Log the exception details for debugging
    // Build a meaningful error response with code, message, status, timestamp, and path
    // Return ResponseEntity with 400 status and error body
    // Spring will fail fast and not call the controller method if required headers are missing. It will even not check for other headers.
    // So, this handler will catch those exceptions and provide a clear error response to the client
    // instead of a generic 500 error.
    @ExceptionHandler(org.springframework.web.bind.MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingRequestHeaderException(org.springframework.web.bind.MissingRequestHeaderException ex, HttpServletRequest request) {
//        logger.info("Entering {}.{}",
//                this.getClass().getSimpleName(),
//                Thread.currentThread().getStackTrace()[1].getMethodName()
//        );
        LogUtils.logMethodEntry(logger, this);
        logger.error(ex.getMessage(), ex);
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "MISSING_HEADER",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    // Handle IllegalArgumentException
    // This is thrown manually in controller for missing headers
    // and can also be thrown by various Java methods for invalid arguments
    // e.g., Integer.parseInt("abc") throws NumberFormatException (subclass of IllegalArgumentException)
    // We can catch IllegalArgumentException to cover a broader range of invalid argument issues
    // and return a 400 Bad Request with details.
    // This ensures clients get meaningful feedback on what went wrong.
    // Note: More specific exceptions should be handled first, as Spring uses the first matching handler
    // in order of declaration.
    // So, if you had a handler for NumberFormatException, it should come before this one.
    // Otherwise, NumberFormatException would be caught here as well since it's a subclass.
    // Always log the exception details for debugging.
    // Build a meaningful error response with code, message, status, timestamp, and path.
    // Return ResponseEntity with 400 status and error body.
    // This improves API usability by providing clear error information.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
//        logger.info("Entering {}.{}",
//                this.getClass().getSimpleName(),
//                Thread.currentThread().getStackTrace()[1].getMethodName()
//        );
        LogUtils.logMethodEntry(logger, this);

        logger.error(ex.getMessage(), ex);
        // Build error response
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                List.of(ex.getMessage()),
                request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }
}

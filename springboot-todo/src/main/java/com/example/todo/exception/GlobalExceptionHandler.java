package com.example.todo.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;

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

    // Handle HandlerMethodValidationException for method parameter validation errors
    // This will catch HandlerMethodValidationException thrown when @Min, @Max etc. on method parameters fail validation.
    // This is a built-in Spring exception.
    // Example usage: In a GET endpoint, if a query parameter annotated with @Min(1) is passed as 0, this exception is thrown.
    // This will result in a 400 Bad Request response with details of the validation errors.
    /* Example JSON response:
      {
        "code": "VALIDATION_FAILED",
        "message": "age must be greater than or equal to 1; page must be greater than or equal to 0",
        "status": 400,
        "timestamp": "2025-09-11T15:22:10.123456"
      }
      In my case it was the following when I passed -1 for a @Min(0) parameter for page number:
        {
	        "code": "VALIDATION_FAILED",
	        "message": "must be greater than or equal to 0",
	        "status": 400,
	        "timestamp": "2025-09-17T13:40:42.194122"
        }
    */
    /*@ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        // Log full exception
        logger.error("Handling HandlerMethodValidationException: ", ex);

        // Collect all validation errors
        List<String> errors = ex.getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .toList();

        ApiErrorResponse error = new ApiErrorResponse(
                "VALIDATION_FAILED",
                String.join("; ", errors),
                HttpStatus.BAD_REQUEST.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }*/
    /*@ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        // Log full exception
        logger.error(">>>>>Handling HandlerMethodValidationException: ", ex);

        // Log each error in detail
        for (MessageSourceResolvable err : ex.getAllErrors()) {
            if (err instanceof ObjectError objectError) {
                logger.error("Error type: {}, error: {}, object name: {}, codes: {}",
                        objectError.getClass().getName(),
                        objectError.getDefaultMessage(),
                        objectError.getObjectName(),
                        Arrays.toString(objectError.getCodes()));
            } else {
                logger.error("Unknown validation error: {}", err);
            }
        }

        // Build detailed error messages
        List<String> errors = ex.getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError fe) {
                        // For @RequestParam/@PathVariable: get param name + message
                        return "Parameter '" + fe.getField() + "' " + fe.getDefaultMessage();
                    } else {
                        return error.getDefaultMessage();
                    }
                })
                .toList();

        String message = String.join("; ", errors);
        // Log the constructed message
        logger.error(">>>>>Validation errors: {}", message);

        ApiErrorResponse error = new ApiErrorResponse(
                "VALIDATION_FAILED",
                message,
                HttpStatus.BAD_REQUEST.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }*/
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        // Log full exception
        logger.error(">>>>>Handling HandlerMethodValidationException: ", ex);
        List<String> errors = ex.getAllErrors().stream()
                .map(error -> {
                    // Try to extract parameter name from arguments
                    String param = null;
                    Object[] arguments = error.getArguments();
                    if (arguments != null) {
                        for (Object arg : arguments) {
                            // Spring passes param info as DefaultMessageSourceResolvable
                            if (arg instanceof DefaultMessageSourceResolvable msr) {
                                // The "default message" is the parameter/field name
                                param = msr.getDefaultMessage();
                                break;
                            }
                        }
                    }
                    return (param != null ? "Parameter '" + param + "' " : "") + error.getDefaultMessage();
                })
                .toList();

        String message = String.join("; ", errors);

        ApiErrorResponse errorBody = new ApiErrorResponse(
                "VALIDATION_FAILED",
                message,
                HttpStatus.BAD_REQUEST.value(),
                java.time.LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody);
    }


    // Handle MethodArgumentNotValidException for @Valid validation errors
    // This will catch MethodArgumentNotValidException thrown when @Valid validation fails on request bodies.
    // This is a built-in Spring exception.
    // Example usage: In a POST endpoint, if the request body fails validation, this exception is thrown.
    // This will result in a 400 Bad Request response with details of the validation errors.
    /* Example JSON response:
      {
        "code": "VALIDATION_FAILED",
        "message": "Validation failed for object='todoRequest'. Error count: 1",
        "status": 400,
        "timestamp": "2025-09-11T15:21:30.987654"
      }
    */

    // src/main/java/com/example/todo/exception/GlobalExceptionHandler.java
//    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentTypeMismatchException.class})
//    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(Exception ex) {
//        ApiErrorResponse error = new ApiErrorResponse("INVALID_ARGUMENT", ex.getMessage(), 400);
//        return ResponseEntity.badRequest().body(error);
//    }

    // HandlerMethodValidationException.class is thrown when @Min, @Max etc. on method parameters fail validation
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        // Log the exception details
        logger.error("Handling MethodArgumentNotValidException in GlobalExceptionHandler: {}", ex.getMessage());
        logger.error("Validation errors: {}", ex.getBindingResult().getAllErrors());
        // Create a custom error response
        ApiErrorResponse error = new ApiErrorResponse("VALIDATION_FAILED", ex.getMessage(), HttpStatus.BAD_REQUEST.value(), java.time.LocalDateTime.now());
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

    /**
     * Handle ResourceNotFoundException
     * This will catch ResourceNotFoundException thrown when a requested resource is not found.
     * This is a custom exception defined in the application.
     * Example usage: In a GET endpoint, if the requested item by ID does not exist, throw this exception.
     * This will result in a 404 Not Found response.
     * <p>
     * Example JSON response:
     * {
     * "code": "NOT_FOUND",
     * "message": "Resource with ID 123 not found",
     * "status": 404,
     * "timestamp": "2025-09-11T15:25:30.654321"
     * }
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

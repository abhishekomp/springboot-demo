package com.example.springrestapidemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * GlobalRestExceptionHandler class
 *
 * @author : kjss920
 * @since : 2025-09-09, Tuesday
 **/
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeaderException(MissingRequestHeaderException  ex) {
        LOGGER.error("MissingRequestHeaderException: {}", ex.getMessage());
        LOGGER.debug("Handling MissingRequestHeaderException", ex);
        ErrorResponse errorResponse = new ErrorResponse("MISSING_HEADER", ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        LOGGER.error("Validation error: {}", ex.getMessage());
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        LOGGER.debug("Handling MethodArgumentNotValidException: {}", msg, ex);
        /*ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR",
                "Validation failed for object='" + ex.getBindingResult().getObjectName() +
                        "'. Error count: " + ex.getBindingResult().getErrorCount(),
                HttpStatus.BAD_REQUEST.value());*/
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR",
                "Validation failed for object='" + ex.getBindingResult().getObjectName() +
                        "'. Error : " + msg,
                HttpStatus.BAD_REQUEST.value());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // DTO for error JSON
    public static class ErrorResponse {
        private final String code;
        private final String message;
        private final int status;

        public ErrorResponse(String code, String message, int status) {
            this.code = code;
            this.message = message;
            this.status = status;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
        public int getStatus() { return status; }
    }

}

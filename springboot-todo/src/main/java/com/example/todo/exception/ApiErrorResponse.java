package com.example.todo.exception;

import java.time.LocalDateTime;

/**
 * ErrorResponse class
 * Custom error response returned to client when things go wrong.
 *
 * @author : kjss920
 * @since : 2025-09-11, Thursday
 **/
public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;

    //Constructor
    public ApiErrorResponse(String code, String message, int status, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
    //Getters
    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public int getStatus() {
        return status;
    }
    // Getter for timestamp
    // The timestamp field is missing in your response because the ErrorResponse class defines timestamp as a private final field but does not provide a public getter for it.
    // Jackson (the JSON serializer used by Spring Boot) only serializes fields that have public getters.
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}

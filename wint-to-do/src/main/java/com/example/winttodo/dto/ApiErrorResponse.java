package com.example.winttodo.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ApiErrorResponse class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/
public class ApiErrorResponse {
    private String code;            // Application error code (e.g., "VALIDATION_FAILED")
    private String message;         // General error message (summary)
    private int status;             // HTTP status code (e.g., 400)
    private LocalDateTime timestamp;
    private List<String> errors;    // List of field-level errors
    private String path;            // Request path that caused the error

    public ApiErrorResponse(String code, String message, int status, LocalDateTime timestamp, List<String> errors, String path) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
        this.errors = errors;
        this.path = path;
    }

    // Getters and setters...
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}

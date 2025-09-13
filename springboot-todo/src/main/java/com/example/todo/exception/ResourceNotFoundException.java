package com.example.todo.exception;

/**
 * ResourceNotFoundException class
 *
 * @author : kjss920
 * @since : 2025-09-13, Saturday
 **/
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

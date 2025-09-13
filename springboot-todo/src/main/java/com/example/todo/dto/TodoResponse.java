package com.example.todo.dto;

/**
 * TodoResponse class
 * This class represents the response object for a Todo item.
 *
 * @author : kjss920
 * @since : 2025-09-13, Saturday
 **/

// TodoResponse.java
public class TodoResponse {
    private final Long id;
    private final String title;
    private final String description;

    // Constructor
    public TodoResponse(Long id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
    // getters
    public Long getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    @Override
    public String toString() {
        return String.format("id: %s, title: %s, description: %s", id, title, description);
    }
}

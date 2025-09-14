package com.example.todo.dto;

import java.util.List;

/**
 * TodoListResponse class
 * This class represents a paginated response for a list of Todo items for the getAll API.
 *
 * @author : kjss920
 * @since : 2025-09-14, Sunday
 **/
public class TodoListResponse {
    private final int count;
    private final List<TodoResponse> items;
    public TodoListResponse(int count, List<TodoResponse> items) {
        this.count = count;
        this.items = items;
    }
    // Getters
    public int getCount() {
        return count;
    }
    public List<TodoResponse> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return String.format("TodoListResponse{count=%d, items=%s}", count, items);
    }
}

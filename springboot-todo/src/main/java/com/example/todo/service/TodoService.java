package com.example.todo.service;

import com.example.todo.dto.TodoListResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.TodoEntity;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    TodoResponse createTodo(TodoRequest request);
    TodoListResponse getAll();
    Optional<TodoResponse> getById(String id);
}

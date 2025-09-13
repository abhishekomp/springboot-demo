package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.TodoEntity;

import java.util.List;
import java.util.Optional;

public interface TodoService {
    TodoResponse createTodo(TodoRequest request);
    List<TodoResponse> getAll();
    Optional<TodoResponse> getById(String id);
}

package com.example.todo.service;

import com.example.todo.dto.TodoListResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TodoService {
    TodoResponse createTodo(TodoRequest request);
    TodoListResponse getAll();
    Page<TodoResponse> getAll(Pageable pageable);
    Optional<TodoResponse> getById(String id);
}

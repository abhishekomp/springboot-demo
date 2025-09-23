package com.example.winttodo.service;

import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TodoService {
    TodoResponse createTodo(TodoRequest todoRequest);
    Page<TodoResponse> getAll(Pageable pageable);
}

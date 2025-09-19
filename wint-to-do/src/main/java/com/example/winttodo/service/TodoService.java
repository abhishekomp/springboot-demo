package com.example.winttodo.service;

import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import com.example.winttodo.model.TodoEntity;

public interface TodoService {
    TodoResponse createTodo(TodoRequest todoRequest);
}

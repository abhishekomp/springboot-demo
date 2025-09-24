package com.example.winttodo.service;

import com.example.winttodo.dto.TodoFullResponse;
import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TodoService {

    TodoResponse createTodo(TodoRequest todoRequest);

    // this method will fetch even the archived todo items.
    List<TodoFullResponse> getAll();

    // this will fetch only the non-archived todo items.
    Page<TodoResponse> findAllByArchivedFalse(Pageable pageable);
}

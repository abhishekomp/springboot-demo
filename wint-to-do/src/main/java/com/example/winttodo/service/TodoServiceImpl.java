package com.example.winttodo.service;

import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import com.example.winttodo.model.TodoEntity;
import com.example.winttodo.repository.TodoRepository;
import org.springframework.stereotype.Service;

/**
 * TodoServiceImpl class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/
@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse createTodo(TodoRequest todoRequest) {
        // Convert DTO to Entity
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTitle(todoRequest.getTitle());
        todoEntity.setDescription(todoRequest.getDescription());
        todoEntity.setTags(todoRequest.getTags());
        todoEntity.setDueDate(todoRequest.getDueDate());

        // save entity to database
        TodoEntity todo = todoRepository.save(todoEntity);
        // convert entity to dto
        return toTodoResponseDto(todo);
    }

    /** Convert Entity to DTO */
    private TodoResponse toTodoResponseDto(TodoEntity todo) {
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(todo.getId());
        todoResponse.setTitle(todo.getTitle());
        todoResponse.setDescription(todo.getDescription());
        todoResponse.setDueDate(todo.getDueDate());
//        todoResponse.setCompleted(todo.isCompleted());
//        todoResponse.setCompletedAt(todo.getCompletedAt());
        todoResponse.setTags(todo.getTags());
        // You can return or use the todoResponse object as needed
        return todoResponse;
    }
}

package com.example.winttodo.service;

import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import com.example.winttodo.model.TodoEntity;
import com.example.winttodo.repository.TodoRepository;
import com.example.winttodo.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * TodoServiceImpl class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/
@Service
public class TodoServiceImpl implements TodoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public TodoResponse createTodo(TodoRequest todoRequest) {
        // log entry to createTodo
        LogUtils.logMethodEntry(logger, this);

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

    // Paginated fetch all todos
   @Override
   public Page<TodoResponse> findAllByArchivedFalse(Pageable pageable) {
         // log entry to createTodo
         LogUtils.logMethodEntry(logger, this);
         // fetch paginated data from repository
         Page<TodoEntity> todoEntityPage = todoRepository.findAllByArchivedFalse(pageable);
         // convert entity to dto
         // This map method applies the given function to each element of the Page and returns a new Page with the transformed elements.
         // The map method is in the Page interface, which is part of the Spring Data Commons library.
         // It is not a default method in Java's Collection framework.
         // The map method is particularly useful for converting entities to DTOs in a paginated response.
         return todoEntityPage.map(this::toTodoResponseDto);
   }

    @Override
    public Page<TodoResponse> getAll(Pageable pageable) {
        // log entry to createTodo
        LogUtils.logMethodEntry(logger, this);
        // fetch paginated data from repository
        Page<TodoEntity> todoEntityPage = todoRepository.findAll(pageable);
        // convert entity to dto
        // This map method applies the given function to each element of the Page and returns a new Page with the transformed elements.
        // The map method is in the Page interface, which is part of the Spring Data Commons library.
        // It is not a default method in Java's Collection framework.
        // The map method is particularly useful for converting entities to DTOs in a paginated response.
        return todoEntityPage.map(this::toTodoResponseDto);
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

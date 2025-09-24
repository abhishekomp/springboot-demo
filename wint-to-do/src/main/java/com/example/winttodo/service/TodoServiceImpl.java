package com.example.winttodo.service;

import com.example.winttodo.dto.TodoFullResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * Fetch all todos including archived ones
     * @return List<TodoResponse>
     */
    @Override
    public List<TodoFullResponse> getAll() {
        // log entry to createTodo
        LogUtils.logMethodEntry(logger, this);
        // fetch paginated data from repository
        List<TodoEntity> todos = todoRepository.findAll();
        // convert entity to dto
        return todos.stream()
                .map(this::toTodoFullResponseDto)
                .collect(Collectors.toCollection(ArrayList::new));
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

    // Convert Entity to TodoFullResponse DTO
    private TodoFullResponse toTodoFullResponseDto(TodoEntity todo) {
        TodoFullResponse todoResponse = new TodoFullResponse();
        todoResponse.setId(todo.getId());
        todoResponse.setTitle(todo.getTitle());
        todoResponse.setDescription(todo.getDescription());
        // Converting LocalDate to String because TodoFullResponse expects dueDate as String. (ISO format) (e.g., "2023-10-05")
        // The dueDate field in TodoEntity is of type LocalDate, which represents a date without time.
        // To convert LocalDate to String in ISO format, we can use the toString() method of LocalDate.
        // The toString() method of LocalDate returns the date in ISO-8601 format (YYYY-MM-DD).
        // Alternatively, we could use DateTimeFormatter to format the date, but for ISO format, toString() is sufficient.
        // Example: LocalDate dueDate = LocalDate.of(2023, 10, 5);
        //          String dueDateString = dueDate.toString(); // "2023-10-05"
        todoResponse.setDueDate(todo.getDueDate().toString());
        todoResponse.setCompleted(todo.isCompleted());
        todoResponse.setArchived(todo.isArchived());
        todoResponse.setTags(todo.getTags());
        // You can return or use the todoResponse object as needed
        return todoResponse;
    }
}

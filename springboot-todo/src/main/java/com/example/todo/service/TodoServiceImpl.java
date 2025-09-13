package com.example.todo.service;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.model.TodoEntity;
import com.example.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer handles business logic
 */
@Service
public class TodoServiceImpl implements TodoService {

    private final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);

    private final TodoRepository repository;

    public TodoServiceImpl(TodoRepository repository) {
        this.repository = repository;
    }

    /**
     * Fetch all To-Do items
     * @return List of TodoResponse
     */
    @Override
    public List<TodoResponse> getAll() {
        logger.info("Fetching all To-Do items");
        // For simplicity, returning a single dummy entity in a list if repository is empty
        if (repository.count() == 0) {
            logger.warn("No To-Do items found, returning a default item");
            // Create a default To-Do item when none exist in the repository
            return List.of(new TodoResponse(1L, "Default To-Do", "This is a default To-Do item."));
        }
        List<TodoEntity> entities = repository.findAll();
        // Stream and map to TodoResponse
        return entities.stream().map(this::toResponse).toList();
    }

    // Fetch by ID
    /**
     * Fetch a To-Do item by its ID
     * @param id The ID of the To-Do item as a String
     * @return Optional containing TodoResponse if found, else empty
     */
    @Override
    public Optional<TodoResponse> getById(String id) {
        logger.info("Fetching To-Do item by ID: {}", id);
        try {
            Long todoId = Long.parseLong(id);
            return repository.findById(todoId).map(this::toResponse);
        } catch (NumberFormatException e) {
            logger.error("Invalid ID format: {}", id);
            return Optional.empty();
        }
    }

    /** Create a new To-Do item
     * @param request The TodoRequest containing title and description
     * @return The created TodoResponse
     */
    @Override
    public TodoResponse createTodo(TodoRequest request) {
        // Log the creation of a new To-Do item
        logger.info("Creating new To-Do item with title: {}", request.getTitle());
        // Create and save the new To-Do entity
        TodoEntity entity = new TodoEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        TodoEntity saved = repository.save(entity);
        logger.info("To-Do item created with ID: {}", saved.getId());

        // Convert to TodoResponse and return
        return toResponse(saved);
    }

    /** Convert TodoEntity to TodoResponse (helper method)
     * @param entity The TodoEntity to convert
     * @return The corresponding TodoResponse
     */
    private TodoResponse toResponse(TodoEntity entity) {
        return new TodoResponse(entity.getId(), entity.getTitle(), entity.getDescription());
    }
}

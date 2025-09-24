package com.example.winttodo.controller;

import com.example.winttodo.dto.TodoFullResponse;
import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import com.example.winttodo.service.TodoService;
import com.example.winttodo.utils.LogUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * TodoController class
 *
 * @author : kjss920
 * @since : 2025-09-17, Wednesday
 **/
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/health")
    String healthCheck() {
        return "Todo API is running";
    }

    @PostMapping()
    ResponseEntity<TodoResponse> createTodo(
            @RequestHeader ("X-Request-Id") String requestId,
            @RequestHeader ("X-Client-Id") String clientId,
            @Valid @RequestBody TodoRequest todoRequest
    ) {
        // Log method entry with headers and body
        //logger.info("Entering createTodo method in {}", this.getClass().getSimpleName());
        logger.info("Entering {}.{}",
                this.getClass().getSimpleName(),
                Thread.currentThread().getStackTrace()[1].getMethodName()
        );

        // validate headers (basic validation)
        validateHeaders(requestId, clientId);

        // Log headers and body
        logger.info("X-Request-Id: {}", requestId);
        logger.info("X-Client-ID: {}", clientId);
        logger.debug("Request Body: {}", todoRequest);

        // Call service to create Todo
        TodoResponse todoResponse = todoService.createTodo(todoRequest);
        logger.info("Created Todo with ID: {}", todoResponse.getId());

        // Add Processed-By header
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Processed-By", "TodoController");

        // Return 201 Created with the created TodoResponse
        // Get URI of the created resource
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(todoResponse.getId())
//                .toUri();

        Long newId = todoResponse.getId();
        // Build the URI for the newly created resource (will go into Location header)
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()  // includes scheme, host, port, context path, current servlet path
                .path("/{id}")         // appends "/{id}"
                .buildAndExpand(newId)
                .toUri();

        return ResponseEntity
                .created(location)  // sets status 201 Created + Location header
                .headers(headers)   // Add custom header
                .body(todoResponse);
    }

    // Pagination endpoint
    @GetMapping()
    ResponseEntity<Page<TodoResponse>> getAllTodosPaginated(
            @RequestHeader ("X-Request-Id") String requestId,
            @RequestHeader ("X-Client-Id") String clientId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        // Log method entry with headers and query params
        /*logger.info("Entering {}.{}",
                this.getClass().getSimpleName(),
                Thread.currentThread().getStackTrace()[1].getMethodName()
        );*/
        LogUtils.logMethodEntry(logger, this);
        // validate headers (basic validation)
        validateHeaders(requestId, clientId);

        // Create Pageable object using PageRequest.of(page, size)
        logger.info("Fetching todos - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Call service to get paginated todos
        Page<TodoResponse> todoPage = todoService.findAllByArchivedFalse(pageable);
        logger.info("Fetched {} todos on page {}", todoPage.getNumberOfElements(), todoPage.getNumber());

        return ResponseEntity.ok()
                .header("X-Processed-By", "TodoController")
                .body(todoPage);
        //return ResponseEntity.ok(todoService.getAll(pageable));

    }

    // getAllTodos without pagination (includes even the archived ones)
    @GetMapping("/all")
    ResponseEntity<Iterable<TodoFullResponse>> getAllTodos(
            @RequestHeader ("X-Request-Id") String requestId,
            @RequestHeader ("X-Client-Id") String clientId
    ) {
        // Log method entry with headers
        /*logger.info("Entering {}.{}",
                this.getClass().getSimpleName(),
                Thread.currentThread().getStackTrace()[1].getMethodName()
        );*/
        LogUtils.logMethodEntry(logger, this);
        // validate headers (basic validation)
        validateHeaders(requestId, clientId);
        logger.info("Fetching all todos (including archived)");
        List<TodoFullResponse> responseList = todoService.getAll();
        logger.info("Fetched todos count: {}", ((Collection<?>) responseList).size());
        return ResponseEntity.ok()
                .header("X-Processed-By", "TodoController")
                .body(responseList);
    }

    /** Basic validation for required headers */
    private void validateHeaders(String requestId, String clientId) {
        StringBuilder missingHeaders = new StringBuilder();
        if (requestId == null || requestId.isEmpty()) {
            missingHeaders.append("X-Request-Id ");
        }
        if (clientId == null || clientId.isEmpty()) {
            missingHeaders.append("X-Client-Id ");
        }
        if (!missingHeaders.isEmpty()) {
            String errorMsg = "Missing required headers: " + missingHeaders.toString().trim();
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
    }

}

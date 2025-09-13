package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.ApiErrorResponse;
import com.example.todo.service.TodoService;
import com.example.todo.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * ToDoController class
 * Example endpoint that requires 2 mandatory headers:
 * - X-Client-Id
 * - X-Request-Id
 * If both present -> return success + a custom response header
 * If missing -> handled by @RestControllerAdvice
 *
 * @author : kjss920
 * @since : 2025-09-11, Thursday
 **/
@RestController
@RequestMapping("/api/todos")
public class TodoController {

    //logger
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);
    // service layer
    private final TodoService service;

    public TodoController(TodoService service) { this.service = service; }

    // GET all todos
    @Operation(summary = "Fetch all To-Do items with mandatory headers",
            description = "Requires X-Client-Id and X-Request-Id headers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponse.class)
                    ),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Processed-By",
                                    description = "Indicates which controller processed the request",
                                    schema = @Schema(type = "string")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing required headers",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class) // Error message as plain string
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/all")
    public ResponseEntity<List<TodoResponse>> getAllTodos(
            @Parameter(description = "Request headers", required = true, in = ParameterIn.HEADER, example = "X-Client-Id: 12345, X-Request-Id: abcde")
            @RequestHeader Map<String, String> headers
    ) {
        // info log the invocation
        logger.info("GET /api/todos invoked");

        //check if headers contain the required headers and log them
        logger.debug("[START] Logging all request headers in getAllTodos method.");
        headers.forEach((key, value) -> logger.debug("In getAllTodos() Header: {} = {}", key, value));
        logger.debug("[END] Logged all request headers in getAllTodos method.");
        // Validate required headers
        // Spring lowercases all header names in the map, so you must use lowercase keys (x-client-id, x-request-id)
        validateHeaders(headers);
        // Log the presence of required headers
        logger.info("Required headers present: X-Client-Id={}, X-Request-Id={}",
                headers.get("x-client-id"), headers.get("x-request-id"));
        // Alternative way to check presence of headers
        /*if (headers.containsKey("x-client-id") && headers.containsKey("x-request-id")) {
            logger.info("Required headers present: X-Client-Id={}, X-Request-Id={}",
                    headers.get("x-client-id"), headers.get("x-request-id"));
        } else {
            logger.error("One or more required headers are missing");
        }*/

        // Proceed with normal processing
        List<TodoResponse> todoResponseList = service.getAll();
        // Add custom header in response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Processed-By",  "TodoController");
        // Return response with 200 OK, body and custom header
        return new ResponseEntity<>(todoResponseList, responseHeaders, HttpStatus.OK);
        //return ResponseEntity.ok().body(todos);
    }

    // GET todo by id
    @Operation(summary = "Fetch To-Do item by ID with mandatory headers",
            description = "Requires X-Client-Id and X-Request-Id headers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved To-Do item",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponse.class)
                    ),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Processed-By",
                                    description = "Indicates which controller processed the request",
                                    schema = @Schema(type = "string")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing required headers",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class) // Error message as plain string
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "To-Do item not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class) // Error message as plain string
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(
            @Parameter(description = "ID of the To-Do item to retrieve", required = true)
            @PathVariable String id,
            @Parameter(description = "Request header", required = true, in = ParameterIn.HEADER, example = "X-Client-Id: 12345")
            @RequestHeader("X-Client-Id") String clientId,
            @Parameter(description = "Request header", required = true, in = ParameterIn.HEADER, example = "X-Request-Id: abcde")
            @RequestHeader("X-Request-Id") String requestId) {
        // info log the invocation
        logger.info("GET /api/todos/{} invoked", id);
        // Convert headers to map for easier validation
        Map<String, String> headers = Map.of(
                "x-client-id", clientId,
                "x-request-id", requestId
        );
        // Validate required headers
        validateHeaders(headers);
        logger.info("Required headers present: X-Client-Id={}, X-Request-Id={}",
                headers.get("x-client-id"), headers.get("x-request-id"));

        // Proceed with normal processing
        // Option 1: Using map and orElse
        // This is more functional style, but can be harder to read for some
        //return service.getById(id)
        //        .map(todo -> ResponseEntity.ok().body(todo))
        //        .orElse(ResponseEntity.notFound().build());
        /*
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        */

        // Option 2: Using isPresent and get
        // This is more imperative style, easier to read for some
        /*
        Optional<TodoResponse> todoOpt = service.getById(id);
        if (todoOpt.isPresent()) {
            return ResponseEntity.ok().body(todoOpt.get());
        } else {
            return ResponseEntity.notFound().build();
        }
        */

        // Option 3: Throw exception if not found and handle globally using @RestControllerAdvice
        // This is more suitable for larger applications where you want consistent error handling
        TodoResponse todo = service.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("To-Do item not found with ID: " + id));

        // Add the custom header in response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Processed-By",  "TodoController");
        //return new ResponseEntity<>(todo, responseHeaders, HttpStatus.OK);
        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(todo);

        //return ResponseEntity.ok().body(todo);
    }

    // POST create todo
    // by default all headers are required, but we can set required=false to make optional
    @Operation(summary = "Create To-Do item with mandatory headers",
            description = "Requires X-Client-Id and X-Request-Id headers")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Successfully created To-Do item",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponse.class)),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Processed-By",
                                    description = "Indicates which controller processed the request",
                                    schema = @Schema(type = "string")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Location",
                                    description = "URI of the newly created To-Do item",
                                    schema = @Schema(type = "string", format = "uri")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing required headers or validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class) // Error message as plain string
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class)
                    )
            )
    })
    @PostMapping("/create")
    public ResponseEntity<TodoResponse> createTodo(
            @Parameter(description = "Request headers", required = true, in = ParameterIn.HEADER, example = "X-Client-Id: 12345, X-Request-Id: abcde")
            @RequestHeader Map<String, String> headers,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "To-Do item to create",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TodoRequest.class)
                    )
            )
            @RequestBody TodoRequest request) {

        // info log the invocation with headers
        logger.info("POST /api/todos/create invoked with headers: {}", headers);

        // debug log all headers
        logger.debug("[START] Logging all request headers in create ToDo method");
        headers.forEach((key, value) -> logger.debug("Header: {} = {}", key, value));
        logger.debug("[END] Logged all request headers in create ToDo method");

        // Validate required headers
        // Spring lowercases all header names in the map, so you must use lowercase keys (x-client-id, x-request-id)
        // String clientId = headers.get("X-Client-Id");
        // String requestId = headers.get("X-Request-Id");

        validateHeaders(headers);

        // Proceed with normal processing
        TodoResponse saved = service.createTodo(request);

        // Add custom header in response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Processed-By",  "TodoController");

        // Log the successful creation
        logger.info("[Controller] Todo created with ID: {}", saved.getId());

        // Return response with 201 Created, body and custom header
        // Also set Location header to point to the newly created resource
        URI location = URI.create("/api/todos/" + saved.getId());
        return ResponseEntity.created(location)
                .headers(responseHeaders)
                .body(saved);
    }

    // Helper method to validate required headers
    // This method checks for the presence of both required headers and collects all missing ones
    // If any are missing, it logs an error and throws IllegalArgumentException with details of all missing headers
    // This allows us to report all missing headers in one go, instead of failing fast on the first missing header
    // This method is called from both GET and POST endpoints
    private void validateHeaders(@RequestHeader Map<String, String> headers) {
        String clientId = headers.get("x-client-id");
        String requestId = headers.get("x-request-id");

        StringBuilder missingHeaders = getMissingHeaders(clientId, requestId);

        if (!missingHeaders.isEmpty()) {
            // Log the missing headers
            logger.error("Missing required headers: {}", missingHeaders.toString().trim());
            // Throw exception with all missing headers
            throw new IllegalArgumentException("Missing required headers: " + missingHeaders.toString().trim());
        }
    }

    // Helper method to collect missing headers
    // Returns a StringBuilder containing names of all missing headers
    private StringBuilder getMissingHeaders(String clientId, String requestId) {
        // Collect missing headers
        StringBuilder missingHeaders = new StringBuilder();
        if (clientId == null) {
            missingHeaders.append("X-Client-Id ");
        }
        if (requestId == null) {
            missingHeaders.append("X-Request-Id ");
        }
        return missingHeaders;
    }


}

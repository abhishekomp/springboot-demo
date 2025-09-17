package com.example.todo.controller;

import com.example.todo.dto.TodoListResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.ApiErrorResponse;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    // Simple health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check endpoint invoked");
        return ResponseEntity.ok("Todo API is up and running!");
    }

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
    public ResponseEntity<TodoListResponse> getAllTodos(
            // The @Parameter annotation is used to document the headers in Swagger UI but there is a gotcha. Spring lowercases all header names in the map, so you must use lowercase keys (x-client-id, x-request-id)
            // It won't work if you are using map
            // You should use @RequestHeader("X-Client-Id") String clientId, @RequestHeader("X-Request-Id") String requestId instead
            // Check my getTodoById method for example where I used individual headers and it works fine in Swagger UI
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
        TodoListResponse listResponse = service.getAll();

        // Log the count of todos retrieved from the service layer
        logger.info("Total To-Do items retrieved: {}", listResponse.getCount());
        logger.trace("[TRACE] Response from service layer: {}", listResponse);

        // Add custom header in response
        // debug log the addition of custom header
        logger.debug("Adding custom response header X-Processed-By: TodoController");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Processed-By",  "TodoController");
        // Return response with 200 OK, body and custom header
        return new ResponseEntity<>(listResponse, responseHeaders, HttpStatus.OK);
        //return ResponseEntity.ok().body(todos);
    }

    // Pagination support in a new endpoint
    // Example: /api/todos/paginated?page=0&size=5
    // Example: /api/todos/paginated?page=1&size=20&sort=createdAt,desc
    // http://localhost:8081/api/todos/paginated?page=1&size=10&sort=id,desc
    // Pageable is an interface provided by Spring Data to encapsulate pagination parameters
    // @PageableDefault sets default page number and size if not provided in request
    // @SortDefault sets default sorting if not provided in request
    // Note: Page number is 0-based index, so page=0 is the first page
    // The response is a Page<TodoResponse> which contains the list of items for the requested page
    // along with pagination metadata like total items, total pages, current page, etc.
    // This is more efficient for large datasets as we are not loading all items into memory
    // and only fetching the required page from the database
    // You can call /api/todos?page=0&size=5 to get first 5 todos
    // Default page=0 and size=10 if not provided
    // You can also call /api/todos?page=1&size=5 to get next 5 todos and so on
    // This is a simple implementation, for large datasets consider using database-level pagination
    // This endpoint is not documented in Swagger for brevity
    // Consider Page<TodoResponse> as List<TodoResponse> with additional pagination metadata.
    @GetMapping("/paginated")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @Parameter(description = "Request headers", required = true, in = ParameterIn.HEADER, example = "X-Client-Id: 12345")
            @RequestHeader("X-Client-Id") String clientId,
            @Parameter(description = "Request header", required = true, in = ParameterIn.HEADER, example = "X-Request-Id: abcde")
            @RequestHeader("X-Request-Id") String requestId,
            @Parameter(description = "Page number for pagination (0-based)", example = "0")
            @PageableDefault(page = 0, size = 10)
                    @SortDefault.SortDefaults({
                            @SortDefault(sort = "id", direction = org.springframework.data.domain.Sort.Direction.ASC)
                    })
            Pageable pageable
    ){
        logger.info("GET /api/todos/paginated invoked with pagination page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        // Validate required headers
        if( clientId == null || clientId.isBlank()) {
            logger.error("Missing required header: X-Client-Id");
            throw new IllegalArgumentException("Missing required header: X-Client-Id");
        }
        if( requestId == null || requestId.isBlank()) {
            logger.error("Missing required header: X-Request-Id");
            throw new IllegalArgumentException("Missing required header: X-Request-Id");
        }
        logger.info("Required headers present: X-Client-Id={}, X-Request-Id={}", clientId, requestId);

        // Proceed with normal processing to fetch paginated todos
        Page<TodoResponse> responsePage = service.getAll(pageable);

        // Log the count of todos retrieved from the service layer
        logger.info("Paginated To-Do items retrieved: {}", responsePage.getTotalElements());
        logger.trace("[TRACE] Paginated response: {}", responsePage);

        // Add custom header in response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Processed-By",  "TodoController");
        // Add pagination metadata headers
        responseHeaders.add("X-Total-Count", String.valueOf(responsePage.getTotalElements()));
        responseHeaders.add("X-Total-Pages", String.valueOf(responsePage.getTotalPages()));
        responseHeaders.add("X-Current-Page", String.valueOf(responsePage.getNumber()));
        responseHeaders.add("X-Page-Size", String.valueOf(responsePage.getSize()));
        // Return response with 200 OK, body and custom header
        return new ResponseEntity<>(responsePage, responseHeaders, HttpStatus.OK);
        //return ResponseEntity.ok().body(responsePage);
    }

    // Improved pagination endpoint with explicit page and size params to validate the page and size.
    // This gives more control over validation and default values
    // Example: /api/todos/paginatedV2?page=0&size=5
    // Example: /api/todos/paginatedV2?page=1&size=20
    // http://localhost:8081/api/todos/paginatedV2?page=1&size=10
    // We cannot validate page value if we use Pageable directly as it defaults to page=0 if not provided or if the value is negative.
    // Hence, we are explicitly getting page and size as request params with validation. Now we can set @Min(0) for page and @Min(1) for size.
    // If client sends a negative page value, Spring will throw 400 Bad Request with validation error message. The actual Exception is MethodArgumentNotValidException which we can handle globally in @RestControllerAdvice.
    // This endpoint is documented in Swagger with detailed response headers for pagination metadata.
    // Note: Page number is 0-based index, so page=0 is the first page
    // The response is a Page<TodoResponse> which contains the list of items for the requested page
    // along with pagination metadata like total items, total pages, current page, etc.
    // This is more efficient for large datasets as we are not loading all items into memory
    // and only fetching the required page from the database.
    @Operation(summary = "Fetch paginated To-Do items with mandatory headers and explicit pagination parameters",
            description = "Requires X-Client-Id and X-Request-Id headers. Use page (0-based) and size query parameters for pagination.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved paginated list",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponse.class)
                    ),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Processed-By",
                                    description = "Indicates which controller processed the request",
                                    schema = @Schema(type = "string")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Total-Count",
                                    description = "Total number of To-Do items",
                                    schema = @Schema(type = "integer")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Total-Pages",
                                    description = "Total number of pages available",
                                    schema = @Schema(type = "integer")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Current-Page",
                                    description = "Current page number (0-based)",
                                    schema = @Schema(type = "integer")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "X-Page-Size",
                                    description = "Number of items per page",
                                    schema = @Schema(type = "integer")
                            )
                    }
            )
    }
    )
    @GetMapping("/paginatedV2")
    public ResponseEntity<Page<TodoResponse>> getTodos(
            @RequestHeader("X-Client-Id") String clientId,      // Required header, if you want optional, set required=false
            @RequestHeader("X-Request-Id") String requestId,    // Required header, Spring will throw 400 Bad Request if missing
            // Explicitly get page and size as request params with validation
            // This gives more control over validation and default values
            @RequestParam("page") @Min(0) int page,
            @RequestParam("size") @Min(1) int size
    ) {
        // info log the invocation
        logger.info("GET /api/todos/paginatedV2 invoked with page={}, size={}", page, size);
        // Validate required headers
        if( clientId == null || clientId.isBlank()) {
            logger.error("Missing required header: X-Client-Id");
            throw new IllegalArgumentException("Missing required header: X-Client-Id");
        }
        if( requestId == null || requestId.isBlank()) {
            logger.error("Missing required header: X-Request-Id");
            throw new IllegalArgumentException("Missing required header: X-Request-Id");
        }
        logger.info("Required headers present: X-Client-Id={}, X-Request-Id={}", clientId, requestId);

        // Create Pageable object manually using PageRequest.of by using the client provided page and size
        // This gives more control over pagination parameters
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<TodoResponse> responsePage = service.getAll(pageable);

        // Add custom header in response
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Processed-By",  "TodoController");
        // Add pagination metadata headers
        responseHeaders.add("X-Total-Count", String.valueOf(responsePage.getTotalElements()));
        responseHeaders.add("X-Total-Pages", String.valueOf(responsePage.getTotalPages()));
        responseHeaders.add("X-Current-Page", String.valueOf(responsePage.getNumber()));
        responseHeaders.add("X-Page-Size", String.valueOf(responsePage.getSize()));
        // Return response with 200 OK, body and custom header
        return new ResponseEntity<>(responsePage, responseHeaders, HttpStatus.OK);
        // ... rest of your code
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

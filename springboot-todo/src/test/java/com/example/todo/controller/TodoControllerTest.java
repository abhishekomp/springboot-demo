package com.example.todo.controller;

import com.example.todo.dto.TodoListResponse;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.is;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoService todoService;

    // Inject logging level from application.properties for verification
    @Value("${logging.level.com.example.todo.exception}")
    private String exceptionLogLevel;

    // Mock the TodoService to isolate controller tests
    @TestConfiguration
    static class TodoControllerTestContextConfiguration {
        @Bean
        protected TodoService todoService() {
            return org.mockito.Mockito.mock(TodoService.class);
        }
    }

    @Test
    void should_checkExceptionPackageLoggingLevelIsINFO() {
        System.out.println("Exception logging level: " + exceptionLogLevel);
        // Should match what's in src/test/resources/application.properties
        // Using AssertJ for assertion (already present in Spring Boot starter test)
        assertThat(exceptionLogLevel).isEqualTo("INFO");
    }

    // Positive Test case: Get All To-Dos with required headers (getting all todos)
    // This test checks for the scenario when both required headers are present.
    @Test
    void should_getAllTodosUsingGETEndpoint() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        TodoResponse response = new TodoResponse(1L, "Test To-Do", "This is a test to-do item.");
        TodoResponse response2 = new TodoResponse(2L, "Another To-Do", "This is another test to-do item.");
        // Create an instance of TodoListResponse
        TodoListResponse listResponse = new TodoListResponse(2, List.of(response,response2));
        // Service layer mock using BDD style
        given(todoService.getAll()).willReturn(listResponse);
        // Act & Assert
        mockMvc.perform(get("/api/todos/all")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.count").value(2)) // Ensure the count is correct (adapted to the new return type from service)
                .andExpect(jsonPath("$.items").isArray()) // Ensure items is a JSON array
                .andExpect(jsonPath("$.items.length()").value(2))
                .andExpect(jsonPath("$.items[0].id").value(1))
                .andExpect(jsonPath("$.items[0].title").value("Test To-Do"))
                .andExpect(jsonPath("$.items[0].description").value("This is a test to-do item."))
                .andExpect(jsonPath("$.items[1].id").value(2))
                .andExpect(jsonPath("$.items[1].title").value("Another To-Do"))
                .andExpect(jsonPath("$.items[1].description").value("This is another test to-do item."));
    }

    // Negative Test case: Get All To-Dos missing required headers (getting all todos)
    // This test checks for the scenario when both required headers are missing.
    @Test
    void should_raiseExceptionForGETAllTodosEndpoint_whenRequestHeadersAreMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/all")
                        // No headers)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("Missing required headers: X-Client-Id X-Request-Id"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Positive Test case: Get All todos for 1st page (page index = 0) using pagination with required headers. Keep the size 2
    @Test
    void should_getTheFirstPageWithTodos_whenInvokedUsingPageAsZeroIndex() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        TodoResponse response = new TodoResponse(1L, "Test To-Do", "This is a test to-do item.");
        TodoResponse response2 = new TodoResponse(2L, "Another To-Do", "This is another test to-do item.");

        // Create Pageable object
        Pageable pageable = PageRequest.of(0, 2); // page 0, size 2

        // Create a Page of TodoResponse
        org.springframework.data.domain.Page<TodoResponse> pageResponse =
                new org.springframework.data.domain.PageImpl<>(List.of(response, response2), pageable, 5); // total 5 items

        // Service layer mock using BDD style
        given(todoService.getAll(any(org.springframework.data.domain.Pageable.class)))
                .willReturn(pageResponse);

        ResultActions resultActions = mockMvc.perform(get("/api/todos/paginatedV2")
                .header("x-client-id", "valid-client-id")
                .header("x-request-id", "valid-request-id")
                .param("page", "0")
                .param("size", "2"));

        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.content").isArray()) // Ensure content is a JSON array
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test To-Do"))
                .andExpect(jsonPath("$.content[0].description").value("This is a test to-do item."))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Another To-Do"))
                .andExpect(jsonPath("$.content[1].description").value("This is another test to-do item."))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").value(5))    // Total elements should be 5 as set in the PageImpl
                .andExpect(jsonPath("$.totalPages").value(3))       // Total pages should be 3 for size 2 and total 5 (Total pages needed for the current total elements)
                .andExpect(jsonPath("$.number").value(0))           // Current page number should be 0
                .andExpect(jsonPath("$.size").value(2));            // Page size should be 2

        // The following is an alternative way to do the same test, commented out for reference
        // Act & Assert
/*        mockMvc.perform(get("/api/todos/paginated")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.content").isArray()) // Ensure content is a JSON array
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Test To-Do"))
                .andExpect(jsonPath("$.content[0].description").value("This is a test to-do item."))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title").value("Another To-Do"))
                .andExpect(jsonPath("$.content[1].description").value("This is another test to-do item."))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.size").value(2));*/
    }

    // Positive Test case: Get Paginated To-dos and validate the middle page number 1 (2nd page) with size 2.
    // We will have in total 5 items with page size = 2 so there will be page 0, 1 and 2 and this test verifies page 1 (aks 2nd page)
    @Test
    void should_testTheMiddlePage_andAssertAboutTheNextPage() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        // --- Prepare data: 4 todos ---
        List<TodoResponse> allTodos = Arrays.asList(
                new TodoResponse(1L, "Todo 1", "Desc 1"),
                new TodoResponse(2L, "Todo 2", "Desc 2"),
                new TodoResponse(3L, "Todo 3", "Desc 3"),
                new TodoResponse(4L, "Todo 4", "Desc 4"),
                new TodoResponse(5L, "Todo 5", "Desc 5")
        );

        // Expected: Page 1, size 2 --> Should contain todos 3 and 4 (zero-based paging)
        List<TodoResponse> pageContent = Arrays.asList(
                new TodoResponse(3L, "Todo 3", "Desc 3"),
                new TodoResponse(4L, "Todo 4", "Desc 4")
        );

        // PageRequest implements Pageable and hence can be used directly for the method call getAll(Pageable pageable)
        PageRequest pageRequest = PageRequest.of(1, 2); // page 1, size 2

        // Create Page object (simulate service layer)
        // This is what the service layer would return to the controller
        Page<TodoResponse> pagedResult = new PageImpl<>(
                pageContent,
                pageRequest,
                allTodos.size() // total = 5
        );

        // --- Mock service ---
        // Using this gives me java.lang.NullPointerException hence use Pageable.class with any().
        //Mockito.when(todoService.getAll(pageRequest)).thenReturn(pagedResult);

        //below works
        //Mockito.when(todoService.getAll(any(Pageable.class))).thenReturn(pagedResult);

        // Alternatively, using BDD style (works fine)
        given(todoService.getAll(any(Pageable.class))).willReturn(pagedResult);

        // --- Perform GET /api/todos?page=1&size=2 ---
        mockMvc.perform(get("/api/todos/paginated") // perform returns ResultActions
                        // Add required headers
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id")
                        // Add pagination params
                        .param("page", "1") // Requesting page 1 (2nd page)
                        .param("size", "2")) // Page size of 2
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.content").isArray()) // Ensure content is a JSON array
                .andExpect(jsonPath("$.content.length()").value(2)) // Should have 2 items
                .andExpect(jsonPath("$.content[0].id").value(3)) // First item on page 1 should be id 3
                .andExpect(jsonPath("$.content[0].title").value("Todo 3"))
                .andExpect(jsonPath("$.content[0].description").value("Desc 3"))
                .andExpect(jsonPath("$.content[1].id").value(4)) // Second item on page 1 should be id 4
                .andExpect(jsonPath("$.content[1].title").value("Todo 4"))
                .andExpect(jsonPath("$.content[1].description").value("Desc 4"))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").value(5)) // Total elements should be 5
                .andExpect(jsonPath("$.totalPages").value(3)) // Total pages should be 5
                .andExpect(jsonPath("$.number").value(1)) // Current page number should be 1 (this is always the page index)
                .andExpect(jsonPath("$.size").value(2)) // Page size should be 2
                .andExpect(jsonPath("$.first").value(false)) // Not the first page
                .andExpect(jsonPath("$.last").value(false)) // Is the last page? No because we have page 2 as well due to 5 items
                .andExpect(jsonPath("$.numberOfElements").value(2)); // Number of elements on this page should be 2
    }

    // Positive test case: Get Paginated To-dos and validate that for total 5 items when we request for page 3 (page index 2) then the page should have only 1 element and it should be the last page.
    @Test
    void should_testTheLastPage_andAssertAboutThePreviousPage() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        // --- Prepare data: 4 todos ---
        List<TodoResponse> allTodos = Arrays.asList(
                new TodoResponse(1L, "Todo 1", "Desc 1"),
                new TodoResponse(2L, "Todo 2", "Desc 2"),
                new TodoResponse(3L, "Todo 3", "Desc 3"),
                new TodoResponse(4L, "Todo 4", "Desc 4"),
                new TodoResponse(5L, "Todo 5", "Desc 5")
        );

        // Expected: Page 2, size 2 --> Should contain todo 5 only (zero-based paging)
        List<TodoResponse> pageContent = List.of(
                new TodoResponse(5L, "Todo 5", "Desc 5")
        );

        // PageRequest implements Pageable and hence can be used directly for the method call getAll(Pageable pageable)
        PageRequest pageRequest = PageRequest.of(2, 2); // page 2 (the 3rd page), size 2

        // Create Page object (simulate service layer)
        // This is what the service layer would return to the controller
        Page<TodoResponse> pagedResult = new PageImpl<>(
                pageContent,
                pageRequest,
                allTodos.size() // total = 5
        );

        // --- Mock service ---
        given(todoService.getAll(any(Pageable.class))).willReturn(pagedResult);

        // --- Perform GET /api/todos?page=2&size=2 ---
        mockMvc.perform(get("/api/todos/paginated") // perform returns ResultActions
                        // Add required headers
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id")
                        // Add pagination params
                        .param("page", "2") // Requesting page 2 (3rd page)
                        .param("size", "2")) // Page size of 2
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.content").isArray()) // Ensure content is a JSON array
                .andExpect(jsonPath("$.content.length()").value(1)) // Should have 1 item (this is the last page with only 1 item)
                .andExpect(jsonPath("$.content[0].id").value(5)) // First item on page 2 should be id 5
                .andExpect(jsonPath("$.content[0].title").value("Todo 5"))
                .andExpect(jsonPath("$.content[0].description").value("Desc 5"))
                .andExpect(jsonPath("$.pageable").exists())
                .andExpect(jsonPath("$.totalElements").value(5)) // Total elements should be 5
                .andExpect(jsonPath("$.totalPages").value(3)) // Total pages should be 3
                .andExpect(jsonPath("$.number").value(2)) // Current page number should be 2 (this is always the page index)
                .andExpect(jsonPath("$.size").value(2)) // Page size should be 2 (this is page size requested not the actual number of elements in the page)
                .andExpect(jsonPath("$.first").value(false)) // Not the first page
                .andExpect(jsonPath("$.last").value(true)) // Is the last page? Yes because we have no more pages after this
                .andExpect(jsonPath("$.numberOfElements").value(1)); // Number of elements on this page should be 1
    }


    // Positive Test case (Edge case): Get Paginated To-dos and validate the page 4 (page index 3) will have no items because we will have in total 5 items with size 2
    // So page index 0 will have item number 1,2 and page 1 will have item number 3,4 and page 2 will have item 5 and page 3 will have no items.
    // Validate that empty should be true
    // Validate that last should be true
    // Validate that first should be false
    // Validate that totalElements should be 5
    // Validate that totalPages should be 3 (this is always total pages needed for the current total elements)
    // Validate that numberOfElements should be 0 (in this page)
    /*
    An example of understanding the pagination response for page index 2 (3rd page) when we have total 4 items and size 2
    1. Total items = 4
    2. Page size = 2
    3. Total pages needed = 4/2 = 2 (page index 0 and 1)
    GET /api/todos?page=2&size=2
    {
	"content": [],              // No items: Page 2 is out of range
	"pageable": {
		"pageNumber": 2,
		"pageSize": 2,
		"offset": 4,
        // ...sort info
	},
	"last": true,               // This is the last page (no pages after this)
	"totalElements": 4,         // Total ToDos overall
	"totalPages": 2,            // Total valid pages (0 and 1)
	"first": false,             // Not the first page
	"size": 2,                  // Page size
	"number": 2,                // Requested page index (2)
	"numberOfElements": 0,      // No items in this page
	"empty": true               // Page is empty
    }
     */
    @Test
    void should_testTheNextPage_andAssertAboutTheNextPage() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        // --- Prepare data: 4 todos ---
        List<TodoResponse> allTodos = Arrays.asList(
                new TodoResponse(1L, "Todo 1", "Desc 1"),
                new TodoResponse(2L, "Todo 2", "Desc 2"),
                new TodoResponse(3L, "Todo 3", "Desc 3"),
                new TodoResponse(4L, "Todo 4", "Desc 4"),
                new TodoResponse(5L, "Todo 5", "Desc 5")
        );

        // Expected: Page 2, size 2 --> Should contain todo 4 only (zero-based paging)
        List<TodoResponse> pageContent = List.of();

        // PageRequest implements Pageable and hence can be used directly for the method call getAll(Pageable pageable)
        PageRequest pageRequest = PageRequest.of(3, 2); // page 3 (this is the index of the page, size 2)

        // Create Page object (simulate service layer)
        // This is what the service layer would return to the controller
        Page<TodoResponse> pagedResult = new PageImpl<>(
                pageContent,
                pageRequest,
                allTodos.size() // total = 5
        );

        // --- Mock service ---
        given(todoService.getAll(any(Pageable.class))).willReturn(pagedResult);

        // --- Perform GET /api/todos?page=2&size=2 ---
        // -- Act & Assert --
        mockMvc.perform(get("/api/todos/paginated")
                        .header("X-Client-Id", "12345")
                        .header("X-Request-Id", "abcde")
                        .param("page", "3") // Requesting page index 3 (4th page)
                        .param("size", "2") // Requesting page size of 2
                        .accept(MediaType.APPLICATION_JSON))
                // Expect HTTP 200 OK
                .andExpect(status().isOk())
                // Expect empty content array
                .andExpect(jsonPath("$.content", hasSize(0)))
                // Expect correct page number (the requested one)
                .andExpect(jsonPath("$.number", is(3)))
                // Page size as requested
                .andExpect(jsonPath("$.size", is(2)))
                // Total elements across all pages
                .andExpect(jsonPath("$.totalElements", is(5)))
                // Total valid pages for size=2 and 5 todos is 3 (pages 0, 1 and 2 hold items)
                .andExpect(jsonPath("$.totalPages", is(3)))
                // Should report 'last' = true
                .andExpect(jsonPath("$.last", is(true)))
                // Should report 'first' = false
                .andExpect(jsonPath("$.first", is(false)))
                // The page is empty
                .andExpect(jsonPath("$.empty", is(true)))
                // There are zero elements in this page content
                .andExpect(jsonPath("$.numberOfElements", is(0)));
    }

    // Negative Test Case for Pagination: Invalid page number (negative)
    // Very important test method. This is very closely related to the GlobalExceptionHandler class
    // This test ensures that if the client sends a negative page number, the controller responds with
    // a 400 Bad Request status and a meaningful error message. Check the GlobalExceptionHandler class for handling this exception
    // This test is important to ensure that the API handles invalid input gracefully and provides clear feedback
    // to the client. It also verifies that the validation annotations on the controller method parameters are
    // working as expected.
    // Take a look at this method in the GlobalExceptionHandler class:
    // public ResponseEntity<ApiErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex)
    @Test
    void should_respondWithBadRequestAndMeaningfulMessage_whenInvalidPageNumberIsProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/paginatedV2")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id")
                        .param("page", "-1") // Invalid negative page number
                        .param("size", "2"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Parameter 'page' must be greater than or equal to 0"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Checking multiple invalid parameters together. Negative page and zero size
    @Test
    void should_respondWithBadRequestAndMeaningfulMessage_whenNegativePageAndZeroSizeAreProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/paginatedV2")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id")
                        .param("page", "-1") // Invalid negative page number
                        .param("size", "0")) // Invalid size (should be > 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                // The message should indicate both validation errors
                .andExpect(jsonPath("$.message").value("Parameter 'page' must be greater than or equal to 0; Parameter 'size' must be greater than or equal to 1"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Checking invalid size (zero) and missing required headers
    // This is currently not handled in the GlobalExceptionHandler class to generate a combined message for different types of errors.
    @Test
    @Disabled
    void should_respondWithBadRequestAndMeaningfulMessage_whenZeroSizeAndMissingHeadersAreProvided() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/paginatedV2")
                        // Missing both required headers
                        .param("page", "0") // Valid page number
                        .param("size", "0")) // Invalid size (should be > 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                // The message should indicate both missing headers and size validation error
                .andExpect(jsonPath("$.message").value("Missing required headers: X-Client-Id X-Request-Id; Parameter 'size' must be greater than or equal to 1"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Positive Test case: Get Single To-Do by ID with required headers (getting todo by id)
    @Test
    void should_getTodoByIdUsingGETEndpoint_whenAllRequiredHeadersAreProvided() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        TodoResponse response = new TodoResponse(1L, "Test To-Do", "This is a test to-do item.");
        // Service layer mock using BDD style
        given(todoService.getById("1")).willReturn(java.util.Optional.of(response));
        // Act & Assert
        mockMvc.perform(get("/api/todos/1")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test To-Do"))
                .andExpect(jsonPath("$.description").value("This is a test to-do item."));
    }

    // Negative Test case: Missing required headers for GET endpoint (getting single todo by id)
    @Test
    void should_raiseExceptionForGETTodoByIdEndpoint_whenRequestHeadersAreMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/1")
                        // No headers
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.message").value("Missing required header: X-Client-Id"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Negative Test case: Missing required header for GET endpoint (getting all todos)
    // This test checks for a single missing header scenario.
    @Test
    void should_respondWithBadRequestAndMeaningfulMessage_whenRequestHeadersIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/all")
                        .header("x-client-id", "valid-client-id")
                        // Missing x-request-id header
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("Missing required headers: X-Request-Id"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Negative Test case: Missing required headers (both headers missing) for POST endpoint (create todo)
    @Test
    void should_raiseExceptionForPOSTCreateTodoEndpoint_whenRequestHeadersAreMissing() throws Exception {
        // Arrange
        // Prepare the request body
        String requestBody = "{\"title\":\"Test To-Do\",\"description\":\"This is a test to-do item.\"}";
        // Act & Assert
        mockMvc.perform(post("/api/todos/create")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("Missing required headers: X-Client-Id X-Request-Id"));
    }

    // Negative Test case: Missing required headers (single headers missing) (create todo)
    @Test
    void should_raiseExceptionForPOSTCreateTodoEndpoint_whenOneRequestHeaderIsMissing() throws Exception {
        // Arrange
        // Prepare the request body
        String requestBody = "{\"title\":\"Test To-Do\",\"description\":\"This is a test to-do item.\"}";
        // Act & Assert
        mockMvc.perform(post("/api/todos/create")
                        .contentType("application/json")
                        .header("x-client-id", "valid-client-id")
                        // Missing x-request-id header
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("Missing required headers: X-Request-Id"));
    }

    // Test case: Successful creation of a to-do item with all required headers (create todo)
    @Test
    void should_createTodoSuccessfully_whenAllRequiredHeadersAreProvided() throws Exception {
        // Arrange
        // Prepare the request body
        // The request body. TodoRequest will be converted to JSON automatically by MockMvc
        // TodoRequest is a representation of the request body for the POST /api/todos/create endpoint
        TodoRequest request = new TodoRequest();
        request.setTitle("Test To-Do");
        request.setDescription("This is a test to-do item.");

        // Prepare the expected response entity
        // The entity that the mocked service will return
        TodoResponse response = new TodoResponse(1L, "Test To-Do", "This is a test to-do item.");

        //given(todoService.createTodo(request)).willReturn(entity);

        // Act
        // Mock the service layer to return the expected entity
        //Mockito.when(todoService.createTodo(request)).thenReturn(entity);
        //Mockito.when(todoService.createTodo(Mockito.any(TodoRequest.class))).thenReturn(response);
        // Use BDD style for better readability
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(response);
        // Perform the POST request and assert the response
        // There is a gotcha here, if you use "startsWith" matcher from Mockito,
        // it conflicts with MockMvc's own matchers. So we use exact match for simplicity.
        // If you want to use "startsWith", you need to use Hamcrest matchers instead.
        // Another gotcha is that header names are case-insensitive, but MockMvc treats them as case-sensitive.
        // In the test, we use lowercase header names to match the controller's behavior, but you can use any case when sending the request via Postman or curl or Insomnia.
        // Assert
        mockMvc.perform(post("/api/todos/create")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id")
                        .contentType("application/json")
                        .content("{\"title\":\"Test To-Do\",\"description\":\"This is a test to-do item.\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(header().string("Location", "/api/todos/1"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test To-Do"))
                .andExpect(jsonPath("$.description").value("This is a test to-do item."));

        // Use mockMvc to perform POST request and assert the response
        /*mockMvc.perform(post("/api/todos/create")
                .header("x-client-id", "valid-client-id")
                .header("x-request-id", "valid-request-id")
                .contentType("application/json")
                .content("{\"title\":\"Test To-Do\",\"description\":\"This is a test to-do item.\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test To-Do"))
                .andExpect(jsonPath("$.description").value("This is a test to-do item."));*/
        // .andExpect(header().string("X-Processed-By", startsWith("TodoController")))
    }
}
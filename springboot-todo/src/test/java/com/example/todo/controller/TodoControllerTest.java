package com.example.todo.controller;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoService todoService;

    // Mock the TodoService to isolate controller tests
    @TestConfiguration
    static class TodoControllerTestContextConfiguration {
        @Bean
        protected TodoService todoService() {
            return org.mockito.Mockito.mock(TodoService.class);
        }
    }

    // Positive Test case: Get All To-Dos with required headers (getting all todos)
    // This test checks for the scenario when both required headers are present.
    @Test
    void test_GetAllTodosWithRequiredHeaders_positiveTest() throws Exception {
        // Arrange
        // Prepare the response from the service layer
        TodoResponse response = new TodoResponse(1L, "Test To-Do", "This is a test to-do item.");
        TodoResponse response2 = new TodoResponse(2L, "Another To-Do", "This is another test to-do item.");
        // Service layer mock using BDD style
        given(todoService.getAll()).willReturn(List.of(response, response2));
        // Act & Assert
        mockMvc.perform(get("/api/todos/all")
                        .header("x-client-id", "valid-client-id")
                        .header("x-request-id", "valid-request-id"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test To-Do"))
                .andExpect(jsonPath("$[0].description").value("This is a test to-do item."))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Another To-Do"))
                .andExpect(jsonPath("$[1].description").value("This is another test to-do item."));
    }

    // Negative Test case: Get All To-Dos missing required headers (getting all todos)
    // This test checks for the scenario when both required headers are missing.
    @Test
    void test_GetAllTodosMissingRequiredHeaders_negativeTest() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/todos/all")
                        // No headers)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"))
                .andExpect(jsonPath("$.message").value("Missing required headers: X-Client-Id X-Request-Id"))
                .andExpect(jsonPath("$.status").value(400));
    }

    // Positive Test case: Get Single To-Do by ID with required headers (getting todo by id)
    @Test
    void test_GetTodoByIdWithRequiredHeaders_positiveTest() throws Exception {
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
    void test_ErrorResponseFromGetByIdEndpointForMissingHeaders_negativeTest() throws Exception {
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
    void test_ErrorResponseFromGetEndpointForMissingHeader_negativeTest() throws Exception {
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
    void test_ErrorResponseFromPostEndpointForMissingHeaders_negativeTest() throws Exception {
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
    void test_ErrorResponseFromPostEndpointForOneMissingHeader_negativeTest() throws Exception {
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
    void createTodo_allHeadersPresent_returnsOk() throws Exception {
        // Test implementation goes here
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
        given(todoService.createTodo(Mockito.any(TodoRequest.class))).willReturn(response);
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
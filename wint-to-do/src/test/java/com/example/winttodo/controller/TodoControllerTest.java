package com.example.winttodo.controller;

import com.example.winttodo.dto.TodoRequest;
import com.example.winttodo.dto.TodoResponse;
import com.example.winttodo.service.TodoService;
import com.example.winttodo.service.TodoServiceImpl;
import com.example.winttodo.utils.MockMvcLoggingUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
//@TestPropertySource(properties = "server.servlet.context-path=/wint")   // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
class TodoControllerTest {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TodoService todoService;

    @TestConfiguration
    static class TodoControllerTestContextConfiguration {
        // Define any additional beans or configurations needed for testing
        @Bean
        protected TodoService todoService() {
            return mock(TodoServiceImpl.class);
        }
    }

    // Positive Test: Test case for createTodo method
    // Scenario: Valid body and valid headers
    // Expected: Return TodoResponse with status 201 Created
    @Test
    void should_returnTodoResponse_whenValidBodyAndValidHeadersForCreateTodoIsUsed() throws Exception {
        // Create the request json body and the expected response
        String requestBody = """
                {
                    "title": "Test Title",
                    "description": "Test Description",
                    "dueDate": "2026-10-15"
                }
                """;

        // Create a TodoResponse that will be returned by the mocked service
        // Take the request body string and convert it to a TodoResponse object
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
//        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        // Convert the request body to a TodoRequest object
        //TodoRequest todoRequest = objectMapper.readValue(requestBody, TodoRequest.class);

        // Create a TodoResponse object based on the TodoRequest
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(1L);
        todoResponse.setTitle("Test Title");
        todoResponse.setDescription("Test Description");
        todoResponse.setDueDate(LocalDate.of(2026, 10, 15));
        //todoResponse.setCompleted(false);
        //todoResponse.setCompletedAt(null);
        todoResponse.setTags(null);

        // Service Mocking
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(todoResponse);

        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add headers and body
                        .header("X-Request-Id", "test-request-id")
                        .header("X-Client-Id", "test-client-id")
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print()) // <-- THIS logs request and response to the console (System.out)
                .andExpect(status().isCreated())    // Assert status 201 Created
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(header().string("Location", "http://localhost/wint/api/todos/1")) // Location header should point to the new resource
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.dueDate").value("2026-10-15"))
                .andExpect(jsonPath("$.tags").value((Object) null))
                .andExpect(jsonPath("$.completed").doesNotExist()) // completed and completedAt are not in the TodoResponse, so they should not exist in the response JSON
                .andExpect(jsonPath("$.completedAt").doesNotExist());
        //.andExpect(jsonPath("$.tags").doesNotExist());

        // Example response body:
        // {"id":1,"title":"Test Title","description":"Test Description","completed":false,"completedAt":null,"tags":null}
    }

    // Positive Test: Additional test case for the createTodo method with more fields in the request body
    // Scenario: Valid body with tags and valid headers
    // Expected: Return TodoResponse with status 201 Created
    @Test
    void should_returnTodoResponseWithTags_whenValidBodyWithTagsAndValidHeaders() throws Exception {
        // Create a TodoResponse with tags that will be returned by the mocked service
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(2L);
        todoResponse.setTitle("Test Title with Tags");
        todoResponse.setDescription("Test Description with Tags");
        todoResponse.setDueDate(LocalDate.of(2027, 1, 1));
        todoResponse.setTags(java.util.List.of("tag1", "tag2"));

        // Service Mocking
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(todoResponse);
        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add headers and body
                        .header("X-Request-Id", "test-request-id-2")
                        .header("X-Client-Id", "test-client-id-2")
                        .contentType("application/json")
                        .content("""
                                {
                                    "title": "Test Title with Tags",
                                    "description": "Test Description with Tags",
                                    "tags": ["tag1", "tag2"],
                                    "completed": false,
                                    "completedAt": null,
                                    "dueDate": "2025-10-15",
                                    "assignedUserId": null,
                                    "archived": false,
                                    "comments": null,
                                    "history": null
                                }
                                """))
                .andDo(print())
                .andExpect(status().isCreated())    // Assert status 201 Created
                .andExpect(content().contentType("application/json"))
                .andExpect(header().exists("X-Processed-By"))
                .andExpect(header().string("X-Processed-By", "TodoController"))
                .andExpect(header().string("Location", "http://localhost/wint/api/todos/2")) // Location header should point to the new resource
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Test Title with Tags"))
                .andExpect(jsonPath("$.description").value("Test Description with Tags"))
                .andExpect(jsonPath("$.dueDate").value("2027-01-01"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("tag1"))
                .andExpect(jsonPath("$.tags[1]").value("tag2"));
    }

    // Negative test case that tests missing headers
    // Scenario: Valid body but missing required headers
    // Expected: Return 400 Bad Request
    @Test
    void should_return400BadRequest_whenValidBodyButMissingRequiredHeaders() throws Exception {
        // Create the request json body
        String requestBody = """
                {
                    "title": "Test Title",
                    "description": "Test Description",
                    "dueDate": "2026-10-15"
                }
                """;
        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add only body, no headers
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())    // Assert status 400 Bad Request
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-Request-Id' for method parameter type String is not present"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0]").value("Required request header 'X-Request-Id' for method parameter type String is not present"))
                .andExpect(jsonPath("$.path").value("/wint/api/todos"));
    }

    // Negative test case that tests invalid body
    // Scenario: Invalid body (missing title) but valid headers
    // Expected: Return 400 Bad Request
    @Test
    void should_return400BadRequest_whenInvalidBodyButValidHeaders() throws Exception {
        // Create the request json body with missing title
        String requestBody = """
                {
                    "description": "Test Description",
                    "dueDate": "2026-10-15"
                }
                """;
        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add headers and body
                        .header("X-Request-Id", "test-request-id")
                        .header("X-Client-Id", "test-client-id")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())    // Assert status 400 Bad Request
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validation failed for: TodoRequest"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").isArray())
                //.andExpect(jsonPath("$.errors[0]").value("title: Title is mandatory"))    // checks literal string, may fail if message changes
                .andExpect(jsonPath("$.errors[0]").value(org.hamcrest.Matchers.containsString("Title is mandatory"))) // checks substring, more robust
                .andExpect(jsonPath("$.path").value("/wint/api/todos"));
    }

    // Additional negative test for createTodo method with no title and past due date
    // Scenario: Invalid body (missing title and past due date) but valid headers
    // Expected: Return 400 Bad Request with multiple validation errors
    @Test
    void should_return400BadRequestWithMultipleErrors_whenInvalidBodyWithNoTitleAndPastDueDateButValidHeaders() throws Exception {
        // Create the request json body with missing title and past due date
        String requestBody = """
                {
                    "description": "Test Description",
                    "dueDate": "2020-01-01"
                }
                """;
        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add headers and body
                        .header("X-Request-Id", "test-request-id")
                        .header("X-Client-Id", "test-client-id")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())    // Assert status 400 Bad Request
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Validation failed for: TodoRequest"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasSize(2))) // Expecting 2 validation errors
                //.andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem("Title is mandatory"))) // Check for title error
                //.andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem("Due date must be in the future"))) // Check for due date error
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("Title is mandatory")))) // Check for title error substring
                .andExpect(jsonPath("$.errors", org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.containsString("Due date must not be in the past")))) // Check for due date error substring
                .andExpect(jsonPath("$.path").value("/wint/api/todos"));
    }

    // This test is just to verify that we are able to write the logs and json payload is pretty printed on the console and in the file if file logging is enabled.
    // It does not assert anything, just prints the logs to the console.
    @Test
    void should_printPrettyJsonInLogs_whenCreatingTodo() throws Exception {
        // Create the request json body and the expected response
        String requestBody = """
                {
                    "title": "Test Title for Logging",
                    "description": "Test Description for Logging",
                    "dueDate": "2026-10-15"
                }
                """;
        // Create a TodoResponse that will be returned by the mocked service
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(3L);
        todoResponse.setTitle("Test Title for Logging");
        todoResponse.setDescription("Test Description for Logging");
        todoResponse.setDueDate(LocalDate.of(2026, 10, 15));
        // Service Mocking using BDD style
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(todoResponse);
        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        MvcResult mvcResult = mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add headers and body
                        .header("X-Request-Id", "test-request-id-3")
                        .header("X-Client-Id", "test-client-id-3")
                        .contentType("application/json")
                        .content(requestBody))
                .andReturn();

        // Assert status 201 Created
        // We are not using andExpect here because we just want to log the request and response
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);

        // We will use the mvcResult to log the request and response using our utility method
        logger.info("==== Request ====");
        logger.info("POST /api/todos");
        logger.info("Headers: Content-Type=application/json");
        logger.info("Body: {}", requestBody);

        logger.info("==== Response ====");
        logger.info("Status: {}", mvcResult.getResponse().getStatus());
        logger.info("Headers: {}", mvcResult.getResponse().getHeaderNames()
                .stream()
                .map(name -> name + "=" + mvcResult.getResponse().getHeader(name))
                .toList());
        logger.info("Body: {}", mvcResult.getResponse().getContentAsString());
    }

    // This test method is an upgrade version of the above method using the Logging utility class to log the request and response json and headers in a pretty format
    @Test
    void should_printPrettyJsonInLogsUsingUtilityMethod_whenCreatingTodo() throws Exception {
        // Create the request json body and the expected response
        String requestBody = """
                {
                    "title": "Test Title for Logging with Utility",
                    "description": "Test Description for Logging with Utility",
                    "dueDate": "2026-10-15"
                }
                """;
        // Create a TodoResponse that will be returned by the mocked service
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setId(4L);
        todoResponse.setTitle("Test Title for Logging with Utility");
        todoResponse.setDescription("Test Description for Logging with Utility");
        todoResponse.setDueDate(LocalDate.of(2026, 10, 15));
        // Service Mocking using BDD style
        given(todoService.createTodo(any(TodoRequest.class))).willReturn(todoResponse);
        // Act & Assert
        // Use mockMvc to perform a POST request and assert the response
        MvcResult mvcResult = mockMvc.perform(post("/wint/api/todos")
                        .contextPath("/wint") // Set context path for testing if you do not have application.properties in src/test/resources otherwise you will face issues with Location Header in the response.
                        // Add headers and body
                        .header("X-Request-Id", "test-request-id-4")
                        .header("X-Client-Id", "test-client-id-4")
                        .contentType("application/json")
                        .content(requestBody))
                .andReturn();
        // Assert status 201 Created
        // We are not using andExpect here because we just want to log the request and response
        assertThat(mvcResult.getResponse().getStatus()).isEqualTo(201);
        // We will use the mvcResult to log the request and response using our utility method
        MockMvcLoggingUtils.logRequestAndResponse(logger, "should_printPrettyJsonInLogsUsingUtilityMethod_whenCreatingTodo",
                "POST", "/wint/api/todos", requestBody, mvcResult);

        // You can check the console or log file to see the pretty printed JSON
        // Perform the assertions as needed
        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertThat(contentAsString).isNotBlank();
        assertThat(contentAsString).contains("Test Title for Logging with Utility");
        assertThat(contentAsString).contains("Test Description for Logging with Utility");
        assertThat(contentAsString).contains("2026-10-15");



        // Get the header from the response and assert
        String locationHeader = mvcResult.getResponse().getHeader("Location");
        assertThat(locationHeader).isEqualTo("http://localhost/wint/api/todos/4");


    }
}
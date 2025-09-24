package com.example.winttodo;

import com.example.winttodo.model.TodoEntity;
import com.example.winttodo.repository.TodoRepository;
import com.example.winttodo.utils.MockMvcLoggingUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class WintToDoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void contextLoads() {
    }

    // Will load the repository with a few todos and then perform a GET of page 0, size 5 and expect to get those todos back. There will be 1 archived todo that should not be returned.
    // Use MockMvc to perform the GET request and verify the response.
    // Use @Sql to load the data before the test and clean up after the test.
    // Use the MockMvcLoggingUtils to log the request and response.
    // Use the Hamcrest matchers to verify the response.
    // Use the ObjectMapper to convert the response to a Java object.
    // Use the PageImpl class to create a page of todos.
    // Use the TodoResponse class to represent the todo in the response.
    // Use the TodoEntity class to represent the todo in the database.
    @Test
    void testGetAllTodosPaginated() throws Exception {
        // Given we have some todos in the database
        // When we perform a GET request to /api/todos?page=0&size=5
        // Then we should get a 200 OK response with a page of todos
        TodoEntity todoEntity = new TodoEntity();
        todoEntity.setTitle("Test Todo");
        todoEntity.setDescription("This is a test todo");
        todoEntity.setArchived(false);
        todoRepository.save(todoEntity);

        // Archived todo
        TodoEntity archivedTodo = new TodoEntity();
        archivedTodo.setTitle("Archived Todo");
        archivedTodo.setDescription("This is an archived todo");
        archivedTodo.setArchived(true);
        todoRepository.save(archivedTodo);

        // Perform GET request and verify response
        MvcResult mvcResult = mockMvc.perform(get("/wint/api/todos")
                        .contextPath("/wint")
                        // Add headers
                        .header("X-Request-Id", "test-request-id")
                        .header("X-Client-Id", "test-client-id")
                        // Add pagination parameters (requesting page 0 with size 5)
                        .param("page", "0")
                        .param("size", "5"))
                .andReturn();

        // Log request and response
        MockMvcLoggingUtils.logRequestAndResponse(
                org.slf4j.LoggerFactory.getLogger(this.getClass()),
                "testGetAllTodosPaginated",
                "GET",
                "/wint/api/todos?page=0&size=5",
                null,
                mvcResult
        );

        // Verify response
        String contentAsString = mvcResult.getResponse().getContentAsString();
        int status = mvcResult.getResponse().getStatus();
//        mvcResult.getResponse().getHeaderNames().forEach(headerName -> {
//            String headerValue = mvcResult.getResponse().getHeader(headerName);
//            System.out.println(headerName + ": " + headerValue);
//        });
        // Extract all headers
        mvcResult.getResponse().getHeader("X-Processed-By");
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode rootNode = mapper.readTree(contentAsString);

        // Assertions for status and content
        org.assertj.core.api.Assertions.assertThat(status)
                .as("status should be 200 but was %d".formatted(status)) // this is shown when the assertion fails
                .isEqualTo(200);
        org.assertj.core.api.Assertions.assertThat(rootNode.get("content").isArray())
                .as("'content' should be an array")     // this is shown when the assertion fails
                .isTrue();
        org.assertj.core.api.Assertions.assertThat(rootNode.get("content").size())
                .as("'content' array should have exactly 1 element") // this is shown when the assertion fails
                .isEqualTo(1);

        // Assertions for Pagination and metadata
        org.assertj.core.api.Assertions.assertThat(rootNode.get("pageable").get("pageNumber").asInt()).isEqualTo(0);    // page number requested
        org.assertj.core.api.Assertions.assertThat(rootNode.get("pageable").get("pageSize").asInt()).isEqualTo(5);  // page size requested
        org.assertj.core.api.Assertions.assertThat(rootNode.get("pageable").get("offset").asInt()).isEqualTo(0);    // offset = pageNumber * pageSize
        org.assertj.core.api.Assertions.assertThat(rootNode.get("totalElements").asInt()).isEqualTo(1);         // total number of elements available (not just in this page)
        org.assertj.core.api.Assertions.assertThat(rootNode.get("totalPages").asInt()).isEqualTo(1);            // total number of pages required to display all elements
        org.assertj.core.api.Assertions.assertThat(rootNode.get("last").asBoolean()).isTrue();                          // is this the last page
        org.assertj.core.api.Assertions.assertThat(rootNode.get("first").asBoolean()).isTrue();                         // is this the first page
        org.assertj.core.api.Assertions.assertThat(rootNode.get("numberOfElements").asInt()).isEqualTo(1);  // number of elements in the current page
        org.assertj.core.api.Assertions.assertThat(rootNode.get("size").asInt()).isEqualTo(5);              // size of the page requested
        org.assertj.core.api.Assertions.assertThat(rootNode.get("number").asInt()).isEqualTo(0);            // current page number
        org.assertj.core.api.Assertions.assertThat(rootNode.get("empty").asBoolean())
                .as("'empty' should be false as there is one todo in the content array") // this is shown when the assertion fails
                .isFalse();                    // is the page empty

        // Assertions for the 1st element in the content array
        JsonNode firstTodo = rootNode.get("content").get(0);
        org.assertj.core.api.Assertions.assertThat(firstTodo.get("id").asLong()).isEqualTo(todoEntity.getId());
        org.assertj.core.api.Assertions.assertThat(firstTodo.get("title").asText()).isEqualTo("Test Todo");
        org.assertj.core.api.Assertions.assertThat(firstTodo.get("description").asText()).isEqualTo("This is a test todo");
        org.assertj.core.api.Assertions.assertThat(firstTodo.get("dueDate").isNull()).isTrue();
        org.assertj.core.api.Assertions.assertThat(firstTodo.get("tags").isArray()).isTrue();
        org.assertj.core.api.Assertions.assertThat(firstTodo.get("tags").size()).isEqualTo(0);

        // Negative assertions for the fields that are expected to be absent in the content array elements
        org.assertj.core.api.Assertions.assertThat(firstTodo.has("archived"))
                .as("'archived' field should be absent in the todo item") // this is shown when the assertion fails
                .isFalse();

        // Extra notes: For this test, we have to write assertions manually because the response is a paginated list and we need to verify the page structure as well as the content.
        // The response structure is:
        /*
       {
            "content" : [ {
                         "id" : 1,
                         "title" : "Test Todo",
                         "description" : "This is a test todo",
                         "dueDate" : null,
                         "tags" : [ ]
                        } ],
            "pageable" : {
                "pageNumber" : 0,
                "pageSize" : 5,
                "sort" : {
                    "empty" : false,
                    "unsorted" : false,
                    "sorted" : true
                },
                "offset" : 0,
                "unpaged" : false,
                "paged" : true
            },
        "last" : true,
        "totalPages" : 1,
        "totalElements" : 1,
        "first" : true,
        "size" : 5,
        "number" : 0,
        "sort" : {
           "empty" : false,
            "unsorted" : false,
            "sorted" : true
        },
        "numberOfElements" : 1,
        "empty" : false
      }
         */
    }
}

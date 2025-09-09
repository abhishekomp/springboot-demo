package com.example.springrestapidemo.controller;

import com.example.springrestapidemo.model.ResourceEntity;
import com.example.springrestapidemo.service.ResourceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
//@Import(GlobalRestExceptionHandler.class)
class ResourceControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ResourceService service;

    // Register the mock as a bean in test context
    @TestConfiguration
    static class ResourceControllerMvcTestConfiguration {
        @Bean
        ResourceService resourceService() {
            return Mockito.mock(ResourceService.class);
        }
    }

    @Test
    void testGetResourcePositive() throws Exception {
        Mockito.when(service.getAll()).thenReturn(List.of(new ResourceEntity("1", "test")));
        assertNotNull(service.getAll());
        assertEquals(1, service.getAll().size());
        mockMvc.perform(get("/api/resources")
                        .header("X-Auth-Token", "valid-token"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertNotNull(json);
                    assertEquals("[{\"id\":\"1\",\"name\":\"test\"}]", json);
                });
    }

    @Test
    void testGetResourceNegative_MissingHeader() throws Exception {
        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-Auth-Token' for method parameter type String is not present"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateResourcePositive() throws Exception {
        // Implement POST test with valid data and headers
        ResourceEntity saved = new ResourceEntity("1", "new-resource");
        Mockito.when(service.save(Mockito.any(ResourceEntity.class))).thenReturn(saved);
        // Further implementation needed for POST request testing

        mockMvc.perform(post("/api/resources")
                    .header("X-Auth-Token", "valid-token")
                    .contentType("application/json")
                    .content("{\"id\":\"1\",\"name\":\"new-resource\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("new-resource"));
    }

    @Test
    void testCreateResource_Negative_Missing_Header() throws Exception {
        mockMvc.perform(post("/api/resources")
                    .contentType("application/json")
                    .content("{\"name\":\"new-resource\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("MISSING_HEADER"))
                .andExpect(jsonPath("$.message").value("Required request header 'X-Auth-Token' for method parameter type String is not present"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testCreateResource_Negative_MissingName_PlainError() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .header("X-Auth-Token", "valid-token")
                        .contentType("application/json")
                        .content("{\"id\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    assertNotNull(json);
                    assertTrue(json.contains("Validation error: name is required"));
                });
    }

    @Test
    void testCreateResource_Negative_MissingName_StructuredErrorInResponse() throws Exception {
        mockMvc.perform(post("/api/resources/alt")
                        .header("X-Auth-Token", "valid-token")
                        .contentType("application/json")
                        .content("{\"id\":\"1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed for object='resourceRequest'. Error : name is required"))
                .andExpect(jsonPath("$.status").value(400));
    }
}
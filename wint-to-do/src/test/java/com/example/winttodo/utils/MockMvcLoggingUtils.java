package com.example.winttodo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Enumeration;
import java.util.stream.Collectors;

public class MockMvcLoggingUtils {
    private static final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    // Pretty logs both request and response (generic version for tests)
    public static void logRequestAndResponse(Logger logger, String testName, String method, String uri, String requestJson, MvcResult result) {
        logger.info("==== [{}] Request ====", testName);
        logger.info("METHOD: {}", method);
        logger.info("URI:    {}", uri);
        //logger.info("Headers: Content-Type=application/json");
        // Log all headers from the request if available
        MockHttpServletRequest request = result.getRequest();
        Enumeration<String> headerNames = result.getRequest().getHeaderNames();
        String requestHeaders = "";
        if (headerNames != null) {
            requestHeaders = java.util.Collections.list(headerNames).stream()
                    .map(name -> name + ": " + request.getHeader(name))
                    .collect(Collectors.joining("; "));
        }
        logger.info("Request Headers: {}", requestHeaders);

        // Log request body if available
        try {
            if (requestJson != null) {
                logger.info("Request Body:\n{}", getPrettyJson(requestJson));
            }
        } catch (Exception e) {
            logger.warn("Failed to pretty-print request JSON", e);
            logger.info("Body: {}", requestJson);
        }

        logger.info("==== [{}] Response ====", testName);
        logger.info("Status: {}", result.getResponse().getStatus());
        String responseHeaders = result.getResponse().getHeaderNames().stream()
                .map(name -> name + ": " + result.getResponse().getHeader(name))
                .collect(Collectors.joining("; "));
        logger.info("Response Headers: {}", responseHeaders);
        try {
            String body = result.getResponse().getContentAsString();
            if (body != null && !body.isBlank()) {
                logger.info("Response Body:\n{}", getPrettyJson(body));
            }
        } catch (Exception e) {
            logger.warn("Failed to pretty-print response JSON", e);
        }
    }

    private static String getPrettyJson(String json) {
        try {
            Object jsonObj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObj);
        } catch (Exception e) {
            // Return as-is if not valid JSON
            return json;
        }
    }
}
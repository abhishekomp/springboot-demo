// src/main/java/com/example/demo/controller/ResourceController.java
package com.example.springrestapidemo.controller;

import com.example.springrestapidemo.model.ResourceEntity;
import com.example.springrestapidemo.model.ResourceRequest;
import com.example.springrestapidemo.service.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
    private final ResourceService service;

    public ResourceController(ResourceService service) { this.service = service; }

    // GET: All or by query param
    @GetMapping
    @Operation(summary = "Fetch data with mandatory headers",
            description = "Requires X-Auth-Token and X-Request-Id headers")
    public ResponseEntity<?> getResources(
            @RequestParam(required = false) String name,
            @Parameter(description = "Auth Token", required = true)
            @RequestHeader(value = "X-Auth-Token") String token,
            @Parameter(description = "Request Id by Client")
            @RequestHeader(value = "X-Request-Id", required = false) String requestId
            ) {

        logger.info("GET invoked with header X-Auth-Token={}", token);
        logger.info("GET invoked with header X-Request-Id={}", requestId);
        logger.info("GET getResources invoked with filter name={}", name);
        if (name == null) {
            logger.info("No filter applied, returning all resources");
            return ResponseEntity.ok(service.getAll());
        } else {
            logger.debug("Filter applied, fetching resources with name={}", name);
            List<ResourceEntity> filtered = service.getAll().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .toList();
            logger.info("Filtered result count: {}", filtered.size());
            return ResponseEntity.ok(filtered);
        }
    }
    // GET by id
    @GetMapping("/{id}")
    @Operation(summary = "Fetch resource by ID with mandatory headers",
            description = "Requires X-Auth-Token header")
    public ResponseEntity<?> getResourceById(@PathVariable String id,
                                             @Parameter(description = "Auth Token", required = true)
                                             @RequestHeader("X-Auth-Token") String token) {
        logger.info("GET by id={} header X-Auth-Token={}", id, token);
        Optional<ResourceEntity> entity = service.getById(id);
        return entity.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // POST (with validation)
    // Since we are using @Valid, if validation fails, Spring will throw MethodArgumentNotValidException
    // We can handle it globally using @ControllerAdvice
    // Here we are using BindingResult to check for validation errors manually
    // If you want to handle it globally, you can remove BindingResult and just use @Valid
    // and create a global exception handler for MethodArgumentNotValidException
    // For simplicity, we are handling it here
    // Also, we are returning 201 Created status code for successful creation
    // and 400 Bad Request for validation errors
    // You can customize the error response as per your requirement
    @PostMapping
    @Operation(summary = "Create resource with mandatory headers",
            description = "Requires X-Auth-Token header")
    public ResponseEntity<?> createResource(@RequestBody @Valid ResourceRequest request, BindingResult result,
                                            @Parameter(description = "Auth Token", required = true)
                                            @RequestHeader("X-Auth-Token") String token) {
        logger.info("POST invoked with header X-Auth-Token={}", token);
        if(result.hasErrors()) {
            String msg = Objects.requireNonNull(result.getFieldError()).getDefaultMessage();
            return ResponseEntity.badRequest().body("Validation error: " + msg);
        }
        ResourceEntity entity = new ResourceEntity(request.getId(), request.getName());
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
        //return ResponseEntity.ok(service.save(entity));
    }

    @PostMapping("/alt")
    @Operation(summary = "Alternate create resource endpoint with structured error response",
            description = "Requires X-Auth-Token header")
    public ResponseEntity<?> createResourceAlt(
            @RequestBody @Valid ResourceRequest request,
            @Parameter(description = "Auth Token", required = true)
            @RequestHeader("X-Auth-Token") String token) {
        logger.info("POST /alt invoked with header X-Auth-Token={}", token);
        ResourceEntity entity = new ResourceEntity(request.getId(), request.getName());
        return new ResponseEntity<>(service.save(entity), HttpStatus.CREATED);
    }

    // PUT with path variable
    @PutMapping("/{id}")
    @Operation(summary = "Update resource by ID with mandatory headers",
            description = "Requires X-Auth-Token header")
    public ResponseEntity<?> updateResource(@PathVariable String id,
                                            @RequestBody ResourceRequest request,
                                            @Parameter(description = "Auth Token", required = true)
                                            @RequestHeader("X-Auth-Token") String token) {
        logger.info("PUT invoked for id={} header X-Auth-Token={}", id, token);
        ResourceEntity entity = service.update(id, request.getName());
        return ResponseEntity.ok(entity);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resource by ID with mandatory headers",
            description = "Requires X-Auth-Token header")
    public ResponseEntity<?> deleteResource(@PathVariable String id,
                                            @Parameter(description = "Auth Token", required = true)
                                            @RequestHeader("X-Auth-Token") String token) {
        logger.info("DELETE invoked for id={} header X-Auth-Token={}", id, token);
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
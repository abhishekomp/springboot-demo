// src/main/java/com/example/demo/model/ResourceRequest.java
package com.example.springrestapidemo.model;
import jakarta.validation.constraints.NotBlank;

public class ResourceRequest {
    @NotBlank(message = "id is required")
    private String id;
    @NotBlank(message = "name is required")
    private String name;

    public ResourceRequest() {}
    public ResourceRequest(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
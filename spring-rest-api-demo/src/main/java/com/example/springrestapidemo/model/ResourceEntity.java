// src/main/java/com/example/demo/model/ResourceEntity.java
package com.example.springrestapidemo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Simple JPA entity with id and name fields. This class is used to represent the resource in the database.
 * It is what the Repository interacts with.
 */
@Entity
public class ResourceEntity {
    @Id
    private String id;
    private String name;

    public ResourceEntity() {}
    public ResourceEntity(String id, String name) { this.id = id; this.name = name; }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}
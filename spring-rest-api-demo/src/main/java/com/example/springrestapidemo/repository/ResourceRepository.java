// src/main/java/com/example/demo/repository/ResourceRepository.java
package com.example.springrestapidemo.repository;
import com.example.springrestapidemo.model.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, String> {
}
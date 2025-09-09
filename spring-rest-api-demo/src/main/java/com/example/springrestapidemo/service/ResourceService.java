// src/main/java/com/example/demo/service/ResourceService.java
package com.example.springrestapidemo.service;

import com.example.springrestapidemo.model.ResourceEntity;
import com.example.springrestapidemo.repository.ResourceRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);
    private final ResourceRepository repo;
    public ResourceService(ResourceRepository repo) { this.repo = repo; }

    public List<ResourceEntity> getAll() {
        logger.debug("Fetching all resources");
        // If findAll has nothing to return then we return a list with single item
        List<ResourceEntity> all = repo.findAll();
        if (all.isEmpty()) {
            logger.debug("No resources found, returning default resource");
            return List.of(new ResourceEntity("default-id", "default-name"));
        }
        return all;
        //return repo.findAll();
    }

    public Optional<ResourceEntity> getById(String id) {
        logger.debug("Fetching resource id={}", id);
        return repo.findById(id);
    }

    public ResourceEntity save(ResourceEntity entity) {
        logger.debug("Saving resource id={}", entity.getId());
        return repo.save(entity);
    }

    public ResourceEntity update(String id, String name) {
        logger.debug("Updating resource id={}", id);
        ResourceEntity entity = new ResourceEntity(id, name);
        return repo.save(entity);
    }

    public void delete(String id) {
        logger.debug("Deleting resource id={}", id);
        repo.deleteById(id);
    }
}
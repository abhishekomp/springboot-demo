package com.example.todo.repository;

import com.example.todo.model.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for TodoEntity
 */
public interface TodoRepository extends JpaRepository<TodoEntity, Long> {}

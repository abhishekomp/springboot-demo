package com.example.winttodo.repository;

import com.example.winttodo.model.TodoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {

    // Derived query method to find all non-archived todos with pagination
    Page<TodoEntity> findAllByArchivedFalse(Pageable pageable);

}

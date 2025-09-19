package com.example.winttodo.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * TodoResponse class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/
public class TodoResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private List<String> tags;

//    private boolean completed;
//    private LocalDateTime completedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public boolean isCompleted() {
//        return completed;
//    }
//
//    public void setCompleted(boolean completed) {
//        this.completed = completed;
//    }
//
//    public LocalDateTime getCompletedAt() {
//        return completedAt;
//    }
//
//    public void setCompletedAt(LocalDateTime completedAt) {
//        this.completedAt = completedAt;
//    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}

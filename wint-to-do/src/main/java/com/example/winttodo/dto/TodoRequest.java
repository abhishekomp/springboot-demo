package com.example.winttodo.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

/**
 * TodoRequest class
 *
 * @author : kjss920
 * @since : 2025-09-18, Thursday
 **/
public class TodoRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;
    private String description;
    @FutureOrPresent(message = "Due date must not be in the past")
    private LocalDate dueDate;
    private List<String> tags;

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

    @Override
    public String toString() {
        return String.format("TodoRequest[title=%s, description=%s, dueDate=%s, tags=%s]", title, description, dueDate, tags);
    }
}

package com.example.winttodo.dto;

/**
 * TodoFullResponse class
 *
 * @author : kjss920
 * @since : 2025-09-24, Wednesday
 **/
public class TodoFullResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private boolean archived;
    private String dueDate;
    private java.util.List<String> tags;

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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public java.util.List<String> getTags() {
        return tags;
    }

    public void setTags(java.util.List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return String.format(
                "TodoFullResponse{id=%d, title='%s', description='%s', completed=%b, archived=%b, dueDate='%s', tags=%s}",
                id, title, description, completed, archived, dueDate, tags
        );
    }
}

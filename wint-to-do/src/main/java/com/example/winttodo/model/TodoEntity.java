package com.example.winttodo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todos")
public class TodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private boolean completed = false;
    private LocalDateTime completedAt;

    private boolean archived = false;

    private Long assignedUserId;
    private LocalDate dueDate;

    @ElementCollection
    @CollectionTable(name = "todo_tags", joinColumns = @JoinColumn(name = "todo_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "todo_comments", joinColumns = @JoinColumn(name = "todo_id"))
    private List<TodoComment> comments = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "todo_history", joinColumns = @JoinColumn(name = "todo_id"))
    private List<TodoHistoryEvent> history = new ArrayList<>();

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public Long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<TodoComment> getComments() { return comments; }
    public void setComments(List<TodoComment> comments) { this.comments = comments; }
    public List<TodoHistoryEvent> getHistory() { return history; }
    public void setHistory(List<TodoHistoryEvent> history) { this.history = history; }

    @Override
    public String toString() {
        return String.format("TodoEntity{id=%d, title='%s', completed=%b}", id, title, completed);
    }
}
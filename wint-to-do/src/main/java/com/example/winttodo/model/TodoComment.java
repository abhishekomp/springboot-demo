package com.example.winttodo.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class TodoComment {
    private String comment;
    private String author;
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

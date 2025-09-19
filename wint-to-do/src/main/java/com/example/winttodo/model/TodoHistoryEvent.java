package com.example.winttodo.model;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class TodoHistoryEvent {
    private String event;
    private LocalDateTime timestamp = LocalDateTime.now();
    private String actor;
    private String details;

    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
package com.example.todo.todobackend.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Entity
@Table(name = "tasks")
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    private boolean completed;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "due_date")
    private Date dueDate;
    
    // Priority enum
    public enum Priority {
        LOW, 
        MEDIUM, 
        HIGH
    }
    
    // Constructors
    public Task() {
        this.priority = Priority.MEDIUM; // Default priority
    }
    
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
        this.priority = Priority.MEDIUM; // Default priority
    }
    
    public Task(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.completed = false;
        this.priority = priority;
    }
    
    public Task(String title, String description, Priority priority, Date dueDate) {
        this.title = title;
        this.description = description;
        this.completed = false;
        this.priority = priority;
        this.dueDate = dueDate;
    }
    
    // Getters and Setters
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
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
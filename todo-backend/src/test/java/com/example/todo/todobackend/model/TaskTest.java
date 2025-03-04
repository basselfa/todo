package com.example.todo.todobackend.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testDefaultConstructor() {
        Task task = new Task();
        assertNull(task.getId());
        assertNull(task.getTitle());
        assertNull(task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(Task.Priority.MEDIUM, task.getPriority());
        assertNull(task.getDueDate());
    }

    @Test
    void testConstructorWithTitleAndDescription() {
        String title = "Test Task";
        String description = "Test Description";
        
        Task task = new Task(title, description);
        
        assertNull(task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(Task.Priority.MEDIUM, task.getPriority());
        assertNull(task.getDueDate());
    }

    @Test
    void testConstructorWithPriority() {
        String title = "Test Task";
        String description = "Test Description";
        Task.Priority priority = Task.Priority.HIGH;
        
        Task task = new Task(title, description, priority);
        
        assertNull(task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(priority, task.getPriority());
        assertNull(task.getDueDate());
    }

    @Test
    void testConstructorWithDueDate() {
        String title = "Test Task";
        String description = "Test Description";
        Task.Priority priority = Task.Priority.LOW;
        Date dueDate = new Date();
        
        Task task = new Task(title, description, priority, dueDate);
        
        assertNull(task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals(priority, task.getPriority());
        assertEquals(dueDate, task.getDueDate());
    }

    @Test
    void testSettersAndGetters() {
        Task task = new Task();
        
        Long id = 1L;
        String title = "Updated Title";
        String description = "Updated Description";
        boolean completed = true;
        Task.Priority priority = Task.Priority.HIGH;
        Date dueDate = new Date();
        
        task.setId(id);
        task.setTitle(title);
        task.setDescription(description);
        task.setCompleted(completed);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        
        assertEquals(id, task.getId());
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(completed, task.isCompleted());
        assertEquals(priority, task.getPriority());
        assertEquals(dueDate, task.getDueDate());
    }
}
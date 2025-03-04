package com.example.todo.todobackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Date;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.model.Task.Priority;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // Spring Data JPA will automatically implement basic CRUD operations
    
    // Find tasks by priority
    List<Task> findByPriority(Priority priority);
    
    // Find tasks by completion status
    List<Task> findByCompleted(boolean completed);
    
    // Find tasks by priority and completion status
    List<Task> findByPriorityAndCompleted(Priority priority, boolean completed);
    
    // Find tasks by due date
    List<Task> findByDueDate(Date dueDate);
    
    // Find tasks with due date before specified date
    List<Task> findByDueDateBefore(Date dueDate);
    
    // Find tasks with due date after specified date
    List<Task> findByDueDateAfter(Date dueDate);
    
    // Find tasks by priority and due date
    List<Task> findByPriorityAndDueDate(Priority priority, Date dueDate);
}
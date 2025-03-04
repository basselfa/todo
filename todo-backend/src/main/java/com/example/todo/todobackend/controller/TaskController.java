package com.example.todo.todobackend.controller;

import java.util.List;
import java.util.Optional;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.model.Task.Priority;
import com.example.todo.todobackend.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    // Get all tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        Optional<Task> task = taskService.getTaskById(id);
        return task.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    // Get tasks by priority
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Priority priority) {
        List<Task> tasks = taskService.getTasksByPriority(priority);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get tasks by completion status
    @GetMapping("/status/{completed}")
    public ResponseEntity<List<Task>> getTasksByCompletionStatus(@PathVariable boolean completed) {
        List<Task> tasks = taskService.getTasksByCompletionStatus(completed);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get tasks by priority and completion status
    @GetMapping("/priority/{priority}/status/{completed}")
    public ResponseEntity<List<Task>> getTasksByPriorityAndStatus(
            @PathVariable Priority priority, 
            @PathVariable boolean completed) {
        List<Task> tasks = taskService.getTasksByPriorityAndStatus(priority, completed);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Create a new task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task newTask = taskService.createTask(task);
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }
    
    // Update an existing task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Optional<Task> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent()) {
            task.setId(id);
            Task updatedTask = taskService.updateTask(task);
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Optional<Task> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent()) {
            taskService.deleteTask(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Mark task as completed
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Task> markTaskCompleted(@PathVariable Long id) {
        Task updatedTask = taskService.markTaskCompleted(id);
        if (updatedTask != null) {
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Mark task as not completed
    @PatchMapping("/{id}/incomplete")
    public ResponseEntity<Task> markTaskNotCompleted(@PathVariable Long id) {
        Task updatedTask = taskService.markTaskNotCompleted(id);
        if (updatedTask != null) {
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Update task priority
    @PatchMapping("/{id}/priority/{priority}")
    public ResponseEntity<Task> updateTaskPriority(
            @PathVariable Long id, 
            @PathVariable Priority priority) {
        Task updatedTask = taskService.updateTaskPriority(id, priority);
        if (updatedTask != null) {
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // Get tasks by due date
    @GetMapping("/due-date/{date}")
    public ResponseEntity<List<Task>> getTasksByDueDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Task> tasks = taskService.getTasksByDueDate(date);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get tasks due before a specific date
    @GetMapping("/due-before/{date}")
    public ResponseEntity<List<Task>> getTasksDueBefore(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Task> tasks = taskService.getTasksDueBefore(date);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get tasks due after a specific date
    @GetMapping("/due-after/{date}")
    public ResponseEntity<List<Task>> getTasksDueAfter(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Task> tasks = taskService.getTasksDueAfter(date);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Get tasks by priority and due date
    @GetMapping("/priority/{priority}/due-date/{date}")
    public ResponseEntity<List<Task>> getTasksByPriorityAndDueDate(
            @PathVariable Priority priority,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Task> tasks = taskService.getTasksByPriorityAndDueDate(priority, date);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
    
    // Update task due date
    @PatchMapping("/{id}/due-date")
    public ResponseEntity<Task> updateTaskDueDate(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dueDate) {
        Task updatedTask = taskService.updateTaskDueDate(id, dueDate);
        if (updatedTask != null) {
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
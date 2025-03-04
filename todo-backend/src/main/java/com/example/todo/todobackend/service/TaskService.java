package com.example.todo.todobackend.service;

import java.util.List;
import java.util.Optional;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.model.Task.Priority;
import com.example.todo.todobackend.repository.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    // Get all tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    // Get a task by ID
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }
    
    // Get tasks by priority
    public List<Task> getTasksByPriority(Priority priority) {
        return taskRepository.findByPriority(priority);
    }
    
    // Get tasks by completion status
    public List<Task> getTasksByCompletionStatus(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }
    
    // Get tasks by priority and completion status
    public List<Task> getTasksByPriorityAndStatus(Priority priority, boolean completed) {
        return taskRepository.findByPriorityAndCompleted(priority, completed);
    }
    
    // Create a new task
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
    
    // Update an existing task
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }
    
    // Delete a task
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
    
    // Mark task as completed
    public Task markTaskCompleted(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(true);
            return taskRepository.save(task);
        }
        return null; // or throw an exception
    }
    
    // Mark task as not completed
    public Task markTaskNotCompleted(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setCompleted(false);
            return taskRepository.save(task);
        }
        return null; // or throw an exception
    }
    
    // Update task priority
    public Task updateTaskPriority(Long id, Priority priority) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setPriority(priority);
            return taskRepository.save(task);
        }
        return null; // or throw an exception
    }
    
    // Get tasks by due date
    public List<Task> getTasksByDueDate(Date dueDate) {
        return taskRepository.findByDueDate(dueDate);
    }
    
    // Get tasks due before a specific date
    public List<Task> getTasksDueBefore(Date date) {
        return taskRepository.findByDueDateBefore(date);
    }
    
    // Get tasks due after a specific date
    public List<Task> getTasksDueAfter(Date date) {
        return taskRepository.findByDueDateAfter(date);
    }
    
    // Get tasks by priority and due date
    public List<Task> getTasksByPriorityAndDueDate(Priority priority, Date dueDate) {
        return taskRepository.findByPriorityAndDueDate(priority, dueDate);
    }
    
    // Update task due date
    public Task updateTaskDueDate(Long id, Date dueDate) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setDueDate(dueDate);
            return taskRepository.save(task);
        }
        return null; // or throw an exception
    }
}
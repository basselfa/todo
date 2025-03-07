package com.example.todo.todobackend.component;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.repository.TaskRepository;
import com.example.todo.todobackend.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Component test for TaskService that uses a real repository with in-memory H2 database.
 * Tests the service and repository integration without mocking the database layer.
 */
@DataJpaTest
@Import(TaskService.class)
@ActiveProfiles("test")
public class TaskServiceComponentTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    private SimpleDateFormat dateFormat;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() throws ParseException {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Create and save test tasks
        task1 = new Task("Component Test Task 1", "Description for task 1");
        task1.setPriority(Task.Priority.HIGH);
        task1.setDueDate(dateFormat.parse("2025-01-15"));
        task1 = taskRepository.save(task1);
        
        task2 = new Task("Component Test Task 2", "Description for task 2");
        task2.setPriority(Task.Priority.MEDIUM);
        task2.setDueDate(dateFormat.parse("2025-02-15"));
        task2.setCompleted(true);
        task2 = taskRepository.save(task2);
        
        task3 = new Task("Component Test Task 3", "Description for task 3");
        task3.setPriority(Task.Priority.LOW);
        task3.setDueDate(dateFormat.parse("2025-03-15"));
        task3 = taskRepository.save(task3);
    }

    @AfterEach
    void tearDown() {
        taskRepository.deleteAll();
    }

    @Test
    void testGetAllTasks() {
        // Act
        List<Task> tasks = taskService.getAllTasks();
        
        // Assert
        assertEquals(3, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Component Test Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Component Test Task 2")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Component Test Task 3")));
    }

    @Test
    void testGetTaskById() {
        // Act
        Optional<Task> foundTask = taskService.getTaskById(task1.getId());
        
        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals("Component Test Task 1", foundTask.get().getTitle());
        assertEquals(Task.Priority.HIGH, foundTask.get().getPriority());
    }

    @Test
    void testGetTasksByPriority() {
        // Act
        List<Task> highPriorityTasks = taskService.getTasksByPriority(Task.Priority.HIGH);
        
        // Assert
        assertEquals(1, highPriorityTasks.size());
        assertEquals("Component Test Task 1", highPriorityTasks.get(0).getTitle());
    }

    @Test
    void testGetTasksByCompletionStatus() {
        // Act
        List<Task> completedTasks = taskService.getTasksByCompletionStatus(true);
        List<Task> incompleteTasks = taskService.getTasksByCompletionStatus(false);
        
        // Assert
        assertEquals(1, completedTasks.size());
        assertEquals(2, incompleteTasks.size());
        assertEquals("Component Test Task 2", completedTasks.get(0).getTitle());
    }

    @Test
    void testCreateTask() throws ParseException {
        // Arrange
        Task newTask = new Task("New Component Test Task", "New description");
        newTask.setPriority(Task.Priority.HIGH);
        newTask.setDueDate(dateFormat.parse("2025-04-15"));
        
        // Act
        Task savedTask = taskService.createTask(newTask);
        
        // Assert
        assertNotNull(savedTask.getId());
        assertEquals("New Component Test Task", savedTask.getTitle());
        
        // Verify it's in the database
        Optional<Task> retrievedTask = taskRepository.findById(savedTask.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("New Component Test Task", retrievedTask.get().getTitle());
    }

    @Test
    void testUpdateTask() {
        // Arrange
        Task taskToUpdate = taskService.getTaskById(task1.getId()).get();
        taskToUpdate.setTitle("Updated Component Test Task");
        taskToUpdate.setDescription("Updated description");
        
        // Act
        Task updatedTask = taskService.updateTask(taskToUpdate);
        
        // Assert
        assertEquals("Updated Component Test Task", updatedTask.getTitle());
        assertEquals("Updated description", updatedTask.getDescription());
        
        // Verify changes are persisted
        Optional<Task> retrievedTask = taskRepository.findById(task1.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("Updated Component Test Task", retrievedTask.get().getTitle());
        assertEquals("Updated description", retrievedTask.get().getDescription());
    }

    @Test
    void testDeleteTask() {
        // Act
        taskService.deleteTask(task1.getId());
        
        // Assert
        Optional<Task> deletedTask = taskRepository.findById(task1.getId());
        assertFalse(deletedTask.isPresent());
        
        // Verify we now have 2 tasks instead of 3
        List<Task> remainingTasks = taskService.getAllTasks();
        assertEquals(2, remainingTasks.size());
    }

    @Test
    void testMarkTaskCompleted() {
        // Act
        Task completedTask = taskService.markTaskCompleted(task1.getId());
        
        // Assert
        assertTrue(completedTask.isCompleted());
        
        // Verify changes are persisted
        Optional<Task> retrievedTask = taskRepository.findById(task1.getId());
        assertTrue(retrievedTask.isPresent());
        assertTrue(retrievedTask.get().isCompleted());
    }

    @Test
    void testMarkTaskNotCompleted() {
        // Act
        Task incompletedTask = taskService.markTaskNotCompleted(task2.getId());
        
        // Assert
        assertFalse(incompletedTask.isCompleted());
        
        // Verify changes are persisted
        Optional<Task> retrievedTask = taskRepository.findById(task2.getId());
        assertTrue(retrievedTask.isPresent());
        assertFalse(retrievedTask.get().isCompleted());
    }

    @Test
    void testUpdateTaskPriority() {
        // Act
        Task updatedTask = taskService.updateTaskPriority(task3.getId(), Task.Priority.HIGH);
        
        // Assert
        assertEquals(Task.Priority.HIGH, updatedTask.getPriority());
        
        // Verify changes are persisted
        Optional<Task> retrievedTask = taskRepository.findById(task3.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals(Task.Priority.HIGH, retrievedTask.get().getPriority());
    }

    @Test
    void testGetTasksByDueDate() throws ParseException {
        // Arrange
        Date dueDate = dateFormat.parse("2025-01-15");
        
        // Act
        List<Task> tasksWithDueDate = taskService.getTasksByDueDate(dueDate);
        
        // Assert
        assertEquals(1, tasksWithDueDate.size());
        assertEquals("Component Test Task 1", tasksWithDueDate.get(0).getTitle());
    }

    @Test
    void testGetTasksDueBefore() throws ParseException {
        // Arrange
        Date date = dateFormat.parse("2025-02-01");
        
        // Act
        List<Task> tasksDueBefore = taskService.getTasksDueBefore(date);
        
        // Assert
        assertEquals(1, tasksDueBefore.size());
        assertEquals("Component Test Task 1", tasksDueBefore.get(0).getTitle());
    }

    @Test
    void testGetTasksDueAfter() throws ParseException {
        // Arrange
        Date date = dateFormat.parse("2025-02-01");
        
        // Act
        List<Task> tasksDueAfter = taskService.getTasksDueAfter(date);
        
        // Assert
        assertEquals(2, tasksDueAfter.size());
        assertTrue(tasksDueAfter.stream().anyMatch(t -> t.getTitle().equals("Component Test Task 2")));
        assertTrue(tasksDueAfter.stream().anyMatch(t -> t.getTitle().equals("Component Test Task 3")));
    }

    @Test
    void testUpdateTaskDueDate() throws ParseException {
        // Arrange
        Date newDueDate = dateFormat.parse("2025-12-31");
        
        // Act
        Task updatedTask = taskService.updateTaskDueDate(task1.getId(), newDueDate);
        
        // Assert
        assertEquals(newDueDate, updatedTask.getDueDate());
        
        // Verify changes are persisted
        Optional<Task> retrievedTask = taskRepository.findById(task1.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals(dateFormat.format(newDueDate), dateFormat.format(retrievedTask.get().getDueDate()));
    }
}
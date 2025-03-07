package com.example.todo.todobackend.component;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Component test for the TaskRepository.
 * Tests the repository with an in-memory H2 database.
 */
@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryComponentTest {

    @Autowired
    private TaskRepository taskRepository;

    private SimpleDateFormat dateFormat;
    private Task task1;
    private Task task2;
    private Task task3;
    private Date today;
    private Date tomorrow;
    private Date yesterday;

    @BeforeEach
    void setUp() throws ParseException {
        // Clear any existing data
        taskRepository.deleteAll();
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = new Date();
        tomorrow = new Date(today.getTime() + 86400000);
        yesterday = new Date(today.getTime() - 86400000);
        
        // Create test tasks
        task1 = new Task("Repository Test Task 1", "Description 1");
        task1.setPriority(Task.Priority.HIGH);
        task1.setDueDate(today);
        
        task2 = new Task("Repository Test Task 2", "Description 2");
        task2.setPriority(Task.Priority.MEDIUM);
        task2.setCompleted(true);
        task2.setDueDate(tomorrow);
        
        task3 = new Task("Repository Test Task 3", "Description 3");
        task3.setPriority(Task.Priority.LOW);
        task3.setDueDate(yesterday);
        
        // Save tasks to the repository
        task1 = taskRepository.save(task1);
        task2 = taskRepository.save(task2);
        task3 = taskRepository.save(task3);
    }

    @Test
    void testFindAll() {
        // Act
        List<Task> tasks = taskRepository.findAll();
        
        // Assert
        assertEquals(3, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 1")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 2")));
        assertTrue(tasks.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 3")));
    }

    @Test
    void testFindById() {
        // Act
        Optional<Task> foundTask = taskRepository.findById(task1.getId());
        
        // Assert
        assertTrue(foundTask.isPresent());
        assertEquals("Repository Test Task 1", foundTask.get().getTitle());
    }

    @Test
    void testFindByPriority() {
        // Act
        List<Task> highPriorityTasks = taskRepository.findByPriority(Task.Priority.HIGH);
        List<Task> mediumPriorityTasks = taskRepository.findByPriority(Task.Priority.MEDIUM);
        List<Task> lowPriorityTasks = taskRepository.findByPriority(Task.Priority.LOW);
        
        // Assert
        assertEquals(1, highPriorityTasks.size());
        assertEquals("Repository Test Task 1", highPriorityTasks.get(0).getTitle());
        
        assertEquals(1, mediumPriorityTasks.size());
        assertEquals("Repository Test Task 2", mediumPriorityTasks.get(0).getTitle());
        
        assertEquals(1, lowPriorityTasks.size());
        assertEquals("Repository Test Task 3", lowPriorityTasks.get(0).getTitle());
    }

    @Test
    void testFindByCompleted() {
        // Act
        List<Task> completedTasks = taskRepository.findByCompleted(true);
        List<Task> incompleteTasks = taskRepository.findByCompleted(false);
        
        // Assert
        assertEquals(1, completedTasks.size());
        assertEquals("Repository Test Task 2", completedTasks.get(0).getTitle());
        
        assertEquals(2, incompleteTasks.size());
        assertTrue(incompleteTasks.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 1")));
        assertTrue(incompleteTasks.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 3")));
    }

    @Test
    void testFindByPriorityAndCompleted() {
        // Act
        List<Task> highIncompleteTasks = taskRepository.findByPriorityAndCompleted(Task.Priority.HIGH, false);
        List<Task> mediumCompletedTasks = taskRepository.findByPriorityAndCompleted(Task.Priority.MEDIUM, true);
        List<Task> lowCompletedTasks = taskRepository.findByPriorityAndCompleted(Task.Priority.LOW, true);
        
        // Assert
        assertEquals(1, highIncompleteTasks.size());
        assertEquals("Repository Test Task 1", highIncompleteTasks.get(0).getTitle());
        
        assertEquals(1, mediumCompletedTasks.size());
        assertEquals("Repository Test Task 2", mediumCompletedTasks.get(0).getTitle());
        
        assertEquals(0, lowCompletedTasks.size());
    }

    @Test
    void testFindByDueDate() {
        // Act
        List<Task> tasksForToday = taskRepository.findByDueDate(today);
        List<Task> tasksForTomorrow = taskRepository.findByDueDate(tomorrow);
        List<Task> tasksForYesterday = taskRepository.findByDueDate(yesterday);
        
        // Assert
        assertEquals(1, tasksForToday.size());
        assertEquals("Repository Test Task 1", tasksForToday.get(0).getTitle());
        
        assertEquals(1, tasksForTomorrow.size());
        assertEquals("Repository Test Task 2", tasksForTomorrow.get(0).getTitle());
        
        assertEquals(1, tasksForYesterday.size());
        assertEquals("Repository Test Task 3", tasksForYesterday.get(0).getTitle());
    }

    @Test
    void testFindByDueDateBefore() {
        // Act
        List<Task> tasksDueBefore = taskRepository.findByDueDateBefore(tomorrow);
        
        // Assert
        assertEquals(2, tasksDueBefore.size());
        assertTrue(tasksDueBefore.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 1")));
        assertTrue(tasksDueBefore.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 3")));
    }

    @Test
    void testFindByDueDateAfter() {
        // Act
        List<Task> tasksDueAfter = taskRepository.findByDueDateAfter(yesterday);
        
        // Assert
        assertEquals(2, tasksDueAfter.size());
        assertTrue(tasksDueAfter.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 1")));
        assertTrue(tasksDueAfter.stream().anyMatch(t -> t.getTitle().equals("Repository Test Task 2")));
    }

    @Test
    void testSaveTask() {
        // Arrange
        Task newTask = new Task("New Repository Task", "New Description");
        newTask.setPriority(Task.Priority.HIGH);
        
        // Act
        Task savedTask = taskRepository.save(newTask);
        
        // Assert
        assertNotNull(savedTask.getId());
        assertEquals("New Repository Task", savedTask.getTitle());
        
        // Verify it's in the database
        Optional<Task> retrievedTask = taskRepository.findById(savedTask.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("New Repository Task", retrievedTask.get().getTitle());
    }

    @Test
    void testUpdateTask() {
        // Arrange
        Task taskToUpdate = taskRepository.findById(task1.getId()).get();
        taskToUpdate.setTitle("Updated Repository Task");
        taskToUpdate.setDescription("Updated Description");
        taskToUpdate.setPriority(Task.Priority.LOW);
        
        // Act
        Task updatedTask = taskRepository.save(taskToUpdate);
        
        // Assert
        assertEquals("Updated Repository Task", updatedTask.getTitle());
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(Task.Priority.LOW, updatedTask.getPriority());
        
        // Verify changes are persisted
        Optional<Task> retrievedTask = taskRepository.findById(task1.getId());
        assertTrue(retrievedTask.isPresent());
        assertEquals("Updated Repository Task", retrievedTask.get().getTitle());
        assertEquals("Updated Description", retrievedTask.get().getDescription());
        assertEquals(Task.Priority.LOW, retrievedTask.get().getPriority());
    }

    @Test
    void testDeleteTask() {
        // Act
        taskRepository.deleteById(task1.getId());
        
        // Assert
        Optional<Task> deletedTask = taskRepository.findById(task1.getId());
        assertFalse(deletedTask.isPresent());
        
        // Verify we now have 2 tasks instead of 3
        List<Task> remainingTasks = taskRepository.findAll();
        assertEquals(2, remainingTasks.size());
    }

    @Test
    void testFindByPriorityAndDueDate() {
        // Act
        List<Task> highPriorityTodayTasks = taskRepository.findByPriorityAndDueDate(Task.Priority.HIGH, today);
        List<Task> mediumPriorityTomorrowTasks = taskRepository.findByPriorityAndDueDate(Task.Priority.MEDIUM, tomorrow);
        
        // Assert
        assertEquals(1, highPriorityTodayTasks.size());
        assertEquals("Repository Test Task 1", highPriorityTodayTasks.get(0).getTitle());
        
        assertEquals(1, mediumPriorityTomorrowTasks.size());
        assertEquals("Repository Test Task 2", mediumPriorityTomorrowTasks.get(0).getTitle());
    }
}
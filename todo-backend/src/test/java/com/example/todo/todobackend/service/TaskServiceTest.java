package com.example.todo.todobackend.service;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private List<Task> taskList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample task
        sampleTask = new Task("Test Task", "Test Description");
        sampleTask.setId(1L);
        sampleTask.setPriority(Task.Priority.MEDIUM);
        sampleTask.setCompleted(false);
        sampleTask.setDueDate(new Date());

        // Create a list of tasks
        taskList = new ArrayList<>();
        taskList.add(sampleTask);
        
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2L);
        task2.setPriority(Task.Priority.HIGH);
        
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3L);
        task3.setPriority(Task.Priority.LOW);
        task3.setCompleted(true);
        
        taskList.add(task2);
        taskList.add(task3);
    }

    @Test
    void testGetAllTasks() {
        when(taskRepository.findAll()).thenReturn(taskList);

        List<Task> result = taskService.getAllTasks();

        assertEquals(3, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void testGetTaskById_ExistingTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Optional<Task> result = taskService.getTaskById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_NonExistingTask() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getTaskById(99L);

        assertFalse(result.isPresent());
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    void testGetTasksByPriority() {
        List<Task> highPriorityTasks = Collections.singletonList(taskList.get(1)); // Task 2 is HIGH priority
        when(taskRepository.findByPriority(Task.Priority.HIGH)).thenReturn(highPriorityTasks);

        List<Task> result = taskService.getTasksByPriority(Task.Priority.HIGH);

        assertEquals(1, result.size());
        assertEquals("Task 2", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByPriority(Task.Priority.HIGH);
    }

    @Test
    void testGetTasksByCompletionStatus() {
        List<Task> completedTasks = Collections.singletonList(taskList.get(2)); // Task 3 is completed
        when(taskRepository.findByCompleted(true)).thenReturn(completedTasks);

        List<Task> result = taskService.getTasksByCompletionStatus(true);

        assertEquals(1, result.size());
        assertEquals("Task 3", result.get(0).getTitle());
        verify(taskRepository, times(1)).findByCompleted(true);
    }

    @Test
    void testGetTasksByPriorityAndStatus() {
        List<Task> highCompletedTasks = new ArrayList<>(); // No tasks match this criteria in our sample
        when(taskRepository.findByPriorityAndCompleted(Task.Priority.HIGH, true)).thenReturn(highCompletedTasks);

        List<Task> result = taskService.getTasksByPriorityAndStatus(Task.Priority.HIGH, true);

        assertEquals(0, result.size());
        verify(taskRepository, times(1)).findByPriorityAndCompleted(Task.Priority.HIGH, true);
    }

    @Test
    void testCreateTask() {
        Task newTask = new Task("New Task", "New Description");
        
        when(taskRepository.save(any(Task.class))).thenReturn(newTask);

        Task result = taskService.createTask(newTask);

        assertEquals("New Task", result.getTitle());
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void testUpdateTask() {
        Task taskToUpdate = new Task("Updated Task", "Updated Description");
        taskToUpdate.setId(1L);
        
        when(taskRepository.save(any(Task.class))).thenReturn(taskToUpdate);

        Task result = taskService.updateTask(taskToUpdate);

        assertEquals("Updated Task", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        verify(taskRepository, times(1)).save(taskToUpdate);
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testMarkTaskCompleted_ExistingTask() {
        Task task = new Task("Test Task", "Test Description");
        task.setId(1L);
        task.setCompleted(false);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            return savedTask;
        });

        Task result = taskService.markTaskCompleted(1L);

        assertNotNull(result);
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testMarkTaskCompleted_NonExistingTask() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Task result = taskService.markTaskCompleted(99L);

        assertNull(result);
        verify(taskRepository, times(1)).findById(99L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testMarkTaskNotCompleted_ExistingTask() {
        Task task = new Task("Test Task", "Test Description");
        task.setId(1L);
        task.setCompleted(true);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            return savedTask;
        });

        Task result = taskService.markTaskNotCompleted(1L);

        assertNotNull(result);
        assertFalse(result.isCompleted());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskPriority_ExistingTask() {
        Task task = new Task("Test Task", "Test Description");
        task.setId(1L);
        task.setPriority(Task.Priority.LOW);
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            return savedTask;
        });

        Task result = taskService.updateTaskPriority(1L, Task.Priority.HIGH);

        assertNotNull(result);
        assertEquals(Task.Priority.HIGH, result.getPriority());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testGetTasksByDueDate() {
        Date dueDate = new Date();
        List<Task> tasksWithDueDate = Collections.singletonList(sampleTask);
        
        when(taskRepository.findByDueDate(dueDate)).thenReturn(tasksWithDueDate);

        List<Task> result = taskService.getTasksByDueDate(dueDate);

        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByDueDate(dueDate);
    }

    @Test
    void testGetTasksDueBefore() {
        Date date = new Date();
        List<Task> tasksDueBefore = Collections.singletonList(sampleTask);
        
        when(taskRepository.findByDueDateBefore(date)).thenReturn(tasksDueBefore);

        List<Task> result = taskService.getTasksDueBefore(date);

        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByDueDateBefore(date);
    }

    @Test
    void testGetTasksDueAfter() {
        Date date = new Date();
        List<Task> tasksDueAfter = Collections.singletonList(sampleTask);
        
        when(taskRepository.findByDueDateAfter(date)).thenReturn(tasksDueAfter);

        List<Task> result = taskService.getTasksDueAfter(date);

        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByDueDateAfter(date);
    }

    @Test
    void testGetTasksByPriorityAndDueDate() {
        Date dueDate = new Date();
        List<Task> tasksByPriorityAndDueDate = Collections.singletonList(sampleTask);
        
        when(taskRepository.findByPriorityAndDueDate(Task.Priority.MEDIUM, dueDate))
            .thenReturn(tasksByPriorityAndDueDate);

        List<Task> result = taskService.getTasksByPriorityAndDueDate(Task.Priority.MEDIUM, dueDate);

        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByPriorityAndDueDate(Task.Priority.MEDIUM, dueDate);
    }

    @Test
    void testUpdateTaskDueDate_ExistingTask() {
        Task task = new Task("Test Task", "Test Description");
        task.setId(1L);
        task.setDueDate(null);
        
        Date newDueDate = new Date();
        
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            return savedTask;
        });

        Task result = taskService.updateTaskDueDate(1L, newDueDate);

        assertNotNull(result);
        assertEquals(newDueDate, result.getDueDate());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
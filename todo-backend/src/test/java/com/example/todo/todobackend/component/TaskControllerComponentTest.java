package com.example.todo.todobackend.component;

import com.example.todo.todobackend.controller.TaskController;
import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Component test for the REST API layer.
 * Tests the controller with a mock service.
 */
@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
public class TaskControllerComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private Task sampleTask;
    private List<Task> taskList;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Create a sample task
        sampleTask = new Task("Component Test API Task", "API Test Description");
        sampleTask.setId(1L);
        sampleTask.setPriority(Task.Priority.MEDIUM);
        sampleTask.setCompleted(false);
        try {
            sampleTask.setDueDate(dateFormat.parse("2025-12-31"));
        } catch (Exception e) {
            // Handle exception
        }

        // Create a list of tasks
        taskList = new ArrayList<>();
        taskList.add(sampleTask);
        
        Task task2 = new Task("API Task 2", "API Description 2");
        task2.setId(2L);
        task2.setPriority(Task.Priority.HIGH);
        
        Task task3 = new Task("API Task 3", "API Description 3");
        task3.setId(3L);
        task3.setPriority(Task.Priority.LOW);
        task3.setCompleted(true);
        
        taskList.add(task2);
        taskList.add(task3);
    }

    @Test
    void testGetAllTasks() throws Exception {
        // Arrange
        when(taskService.getAllTasks()).thenReturn(taskList);

        // Act & Assert
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Component Test API Task")))
                .andExpect(jsonPath("$[1].title", is("API Task 2")))
                .andExpect(jsonPath("$[2].title", is("API Task 3")));

        // Verify
        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testCreateTask() throws Exception {
        // Arrange
        Task taskToCreate = new Task("New API Task", "New API Description");
        taskToCreate.setPriority(Task.Priority.HIGH);
        
        Task createdTask = new Task("New API Task", "New API Description");
        createdTask.setId(4L);
        createdTask.setPriority(Task.Priority.HIGH);
        
        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        // Act & Assert
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.title", is("New API Task")))
                .andExpect(jsonPath("$.description", is("New API Description")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        // Verify
        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void testGetTaskById_ExistingTask() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));

        // Act & Assert
        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Component Test API Task")))
                .andExpect(jsonPath("$.description", is("API Test Description")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.priority", is("MEDIUM")));

        // Verify
        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testGetTaskById_NonExistingTask() throws Exception {
        // Arrange
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound());

        // Verify
        verify(taskService, times(1)).getTaskById(99L);
    }

    @Test
    void testUpdateTask_ExistingTask() throws Exception {
        // Arrange
        Task taskToUpdate = new Task("Updated API Task", "Updated API Description");
        taskToUpdate.setId(1L);
        taskToUpdate.setPriority(Task.Priority.HIGH);
        
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskService.updateTask(any(Task.class))).thenReturn(taskToUpdate);

        // Act & Assert
        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated API Task")))
                .andExpect(jsonPath("$.description", is("Updated API Description")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        // Verify
        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).updateTask(any(Task.class));
    }

    @Test
    void testDeleteTask_ExistingTask() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskService).deleteTask(1L);

        // Act & Assert
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        // Verify
        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void testMarkTaskCompleted() throws Exception {
        // Arrange
        Task completedTask = new Task("Component Test API Task", "API Test Description");
        completedTask.setId(1L);
        completedTask.setCompleted(true);
        
        when(taskService.markTaskCompleted(1L)).thenReturn(completedTask);

        // Act & Assert
        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(true)));

        // Verify
        verify(taskService, times(1)).markTaskCompleted(1L);
    }

    @Test
    void testGetTasksByPriority() throws Exception {
        // Arrange
        List<Task> highPriorityTasks = Collections.singletonList(taskList.get(1)); // Task 2 is HIGH priority
        when(taskService.getTasksByPriority(Task.Priority.HIGH)).thenReturn(highPriorityTasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("API Task 2")))
                .andExpect(jsonPath("$[0].priority", is("HIGH")));

        // Verify
        verify(taskService, times(1)).getTasksByPriority(Task.Priority.HIGH);
    }

    @Test
    void testGetTasksByCompletionStatus() throws Exception {
        // Arrange
        List<Task> completedTasks = Collections.singletonList(taskList.get(2)); // Task 3 is completed
        when(taskService.getTasksByCompletionStatus(true)).thenReturn(completedTasks);

        // Act & Assert
        mockMvc.perform(get("/tasks/status/true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("API Task 3")))
                .andExpect(jsonPath("$[0].completed", is(true)));

        // Verify
        verify(taskService, times(1)).getTasksByCompletionStatus(true);
    }

    @Test
    void testGetTasksByDueDate() throws Exception {
        // Arrange
        Date dueDate = dateFormat.parse("2025-12-31");
        List<Task> tasksWithDueDate = Collections.singletonList(sampleTask);
        
        when(taskService.getTasksByDueDate(any(Date.class))).thenReturn(tasksWithDueDate);

        // Act & Assert
        mockMvc.perform(get("/tasks/due-date/2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Component Test API Task")));

        // Verify
        verify(taskService, times(1)).getTasksByDueDate(any(Date.class));
    }

    @Test
    void testInvalidRequestHandling() throws Exception {
        // Test invalid JSON
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest());
                
        // Test invalid priority
        mockMvc.perform(get("/tasks/priority/INVALID_PRIORITY"))
                .andExpect(status().isBadRequest());
                
        // Test invalid date format
        mockMvc.perform(get("/tasks/due-date/not-a-date"))
                .andExpect(status().isBadRequest());
    }
}
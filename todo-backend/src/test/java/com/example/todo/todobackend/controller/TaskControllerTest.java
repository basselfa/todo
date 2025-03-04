package com.example.todo.todobackend.controller;

import com.example.todo.todobackend.model.Task;
import com.example.todo.todobackend.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // TODO: Replace with alternative in future Spring Boot versions
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
        sampleTask = new Task("Test Task", "Test Description");
        sampleTask.setId(1L);
        sampleTask.setPriority(Task.Priority.MEDIUM);
        sampleTask.setCompleted(false);
        try {
            sampleTask.setDueDate(dateFormat.parse("2023-12-31"));
        } catch (Exception e) {
            // Handle exception
        }

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
    void testGetAllTasks() throws Exception {
        when(taskService.getAllTasks()).thenReturn(taskList);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[1].title", is("Task 2")))
                .andExpect(jsonPath("$[2].title", is("Task 3")));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    void testGetTaskById_ExistingTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.priority", is("MEDIUM")));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    void testGetTaskById_NonExistingTask() throws Exception {
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(99L);
    }

    @Test
    void testGetTasksByPriority() throws Exception {
        List<Task> highPriorityTasks = Collections.singletonList(taskList.get(1)); // Task 2 is HIGH priority
        when(taskService.getTasksByPriority(Task.Priority.HIGH)).thenReturn(highPriorityTasks);

        mockMvc.perform(get("/tasks/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Task 2")))
                .andExpect(jsonPath("$[0].priority", is("HIGH")));

        verify(taskService, times(1)).getTasksByPriority(Task.Priority.HIGH);
    }

    @Test
    void testGetTasksByCompletionStatus() throws Exception {
        List<Task> completedTasks = Collections.singletonList(taskList.get(2)); // Task 3 is completed
        when(taskService.getTasksByCompletionStatus(true)).thenReturn(completedTasks);

        mockMvc.perform(get("/tasks/status/true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Task 3")))
                .andExpect(jsonPath("$[0].completed", is(true)));

        verify(taskService, times(1)).getTasksByCompletionStatus(true);
    }

    @Test
    void testGetTasksByPriorityAndStatus() throws Exception {
        List<Task> highCompletedTasks = new ArrayList<>(); // No tasks match this criteria in our sample
        when(taskService.getTasksByPriorityAndStatus(Task.Priority.HIGH, true)).thenReturn(highCompletedTasks);

        mockMvc.perform(get("/tasks/priority/HIGH/status/true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(taskService, times(1)).getTasksByPriorityAndStatus(Task.Priority.HIGH, true);
    }

    @Test
    void testCreateTask() throws Exception {
        Task taskToCreate = new Task("New Task", "New Description");
        taskToCreate.setPriority(Task.Priority.HIGH);
        
        Task createdTask = new Task("New Task", "New Description");
        createdTask.setId(4L);
        createdTask.setPriority(Task.Priority.HIGH);
        
        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(4)))
                .andExpect(jsonPath("$.title", is("New Task")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void testUpdateTask_ExistingTask() throws Exception {
        Task taskToUpdate = new Task("Updated Task", "Updated Description");
        taskToUpdate.setId(1L);
        taskToUpdate.setPriority(Task.Priority.HIGH);
        
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskService.updateTask(any(Task.class))).thenReturn(taskToUpdate);

        mockMvc.perform(put("/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Updated Task")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).updateTask(any(Task.class));
    }

    @Test
    void testUpdateTask_NonExistingTask() throws Exception {
        Task taskToUpdate = new Task("Updated Task", "Updated Description");
        taskToUpdate.setId(99L);
        
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/tasks/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskToUpdate)))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(99L);
        verify(taskService, never()).updateTask(any(Task.class));
    }

    @Test
    void testDeleteTask_ExistingTask() throws Exception {
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).deleteTask(1L);
    }

    @Test
    void testDeleteTask_NonExistingTask() throws Exception {
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).getTaskById(99L);
        verify(taskService, never()).deleteTask(anyLong());
    }

    @Test
    void testMarkTaskCompleted_ExistingTask() throws Exception {
        Task completedTask = new Task("Test Task", "Test Description");
        completedTask.setId(1L);
        completedTask.setCompleted(true);
        
        when(taskService.markTaskCompleted(1L)).thenReturn(completedTask);

        mockMvc.perform(patch("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(true)));

        verify(taskService, times(1)).markTaskCompleted(1L);
    }

    @Test
    void testMarkTaskCompleted_NonExistingTask() throws Exception {
        when(taskService.markTaskCompleted(99L)).thenReturn(null);

        mockMvc.perform(patch("/tasks/99/complete"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).markTaskCompleted(99L);
    }

    @Test
    void testMarkTaskNotCompleted_ExistingTask() throws Exception {
        Task incompletedTask = new Task("Test Task", "Test Description");
        incompletedTask.setId(1L);
        incompletedTask.setCompleted(false);
        
        when(taskService.markTaskNotCompleted(1L)).thenReturn(incompletedTask);

        mockMvc.perform(patch("/tasks/1/incomplete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.completed", is(false)));

        verify(taskService, times(1)).markTaskNotCompleted(1L);
    }

    @Test
    void testUpdateTaskPriority_ExistingTask() throws Exception {
        Task updatedTask = new Task("Test Task", "Test Description");
        updatedTask.setId(1L);
        updatedTask.setPriority(Task.Priority.HIGH);
        
        when(taskService.updateTaskPriority(1L, Task.Priority.HIGH)).thenReturn(updatedTask);

        mockMvc.perform(patch("/tasks/1/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.priority", is("HIGH")));

        verify(taskService, times(1)).updateTaskPriority(1L, Task.Priority.HIGH);
    }

    @Test
    void testGetTasksByDueDate() throws Exception {
        Date dueDate = dateFormat.parse("2023-12-31");
        List<Task> tasksWithDueDate = Collections.singletonList(sampleTask);
        
        when(taskService.getTasksByDueDate(eq(dueDate))).thenReturn(tasksWithDueDate);

        mockMvc.perform(get("/tasks/due-date/2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")));

        verify(taskService, times(1)).getTasksByDueDate(any(Date.class));
    }

    @Test
    void testGetTasksDueBefore() throws Exception {
        Date date = dateFormat.parse("2024-01-01");
        List<Task> tasksDueBefore = Collections.singletonList(sampleTask);
        
        when(taskService.getTasksDueBefore(eq(date))).thenReturn(tasksDueBefore);

        mockMvc.perform(get("/tasks/due-before/2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")));

        verify(taskService, times(1)).getTasksDueBefore(any(Date.class));
    }

    @Test
    void testGetTasksDueAfter() throws Exception {
        Date date = dateFormat.parse("2023-12-30");
        List<Task> tasksDueAfter = Collections.singletonList(sampleTask);
        
        when(taskService.getTasksDueAfter(eq(date))).thenReturn(tasksDueAfter);

        mockMvc.perform(get("/tasks/due-after/2023-12-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")));

        verify(taskService, times(1)).getTasksDueAfter(any(Date.class));
    }

    @Test
    void testGetTasksByPriorityAndDueDate() throws Exception {
        Date date = dateFormat.parse("2023-12-31");
        List<Task> tasksByPriorityAndDueDate = Collections.singletonList(sampleTask);
        
        when(taskService.getTasksByPriorityAndDueDate(eq(Task.Priority.MEDIUM), eq(date)))
            .thenReturn(tasksByPriorityAndDueDate);

        mockMvc.perform(get("/tasks/priority/MEDIUM/due-date/2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Test Task")))
                .andExpect(jsonPath("$[0].priority", is("MEDIUM")));

        verify(taskService, times(1)).getTasksByPriorityAndDueDate(eq(Task.Priority.MEDIUM), any(Date.class));
    }

    @Test
    void testUpdateTaskDueDate_ExistingTask() throws Exception {
        Date newDueDate = dateFormat.parse("2024-01-15");
        
        Task updatedTask = new Task("Test Task", "Test Description");
        updatedTask.setId(1L);
        updatedTask.setDueDate(newDueDate);
        
        when(taskService.updateTaskDueDate(eq(1L), any(Date.class))).thenReturn(updatedTask);

        mockMvc.perform(patch("/tasks/1/due-date")
                .param("dueDate", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));

        verify(taskService, times(1)).updateTaskDueDate(eq(1L), any(Date.class));
    }

    @Test
    void testUpdateTaskDueDate_NonExistingTask() throws Exception {
        when(taskService.updateTaskDueDate(eq(99L), any(Date.class))).thenReturn(null);

        mockMvc.perform(patch("/tasks/99/due-date")
                .param("dueDate", "2024-01-15"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).updateTaskDueDate(eq(99L), any(Date.class));
    }
}
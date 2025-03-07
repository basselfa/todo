package com.example.todo.todobackend.integration;

import com.example.todo.todobackend.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for the Todo application.
 * Tests the entire application stack from controller to database.
 * Uses a real embedded database and real application context.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TodoAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    void testFullTaskLifecycle() throws Exception {
        // 1. Create a new task
        Task newTask = new Task("Integration Test Task", "Task created during integration test");
        newTask.setPriority(Task.Priority.HIGH);
        newTask.setDueDate(new Date());

        MvcResult createResult = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Integration Test Task")))
                .andExpect(jsonPath("$.description", is("Task created during integration test")))
                .andExpect(jsonPath("$.priority", is("HIGH")))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andReturn();

        // Extract the created task ID
        String responseContent = createResult.getResponse().getContentAsString();
        Task createdTask = objectMapper.readValue(responseContent, Task.class);
        Long taskId = createdTask.getId();

        // 2. Get the task by ID
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId.intValue())))
                .andExpect(jsonPath("$.title", is("Integration Test Task")));

        // 3. Update the task
        createdTask.setTitle("Updated Integration Test Task");
        createdTask.setDescription("Updated description");

        mockMvc.perform(put("/tasks/" + taskId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Test Task")))
                .andExpect(jsonPath("$.description", is("Updated description")));

        // 4. Mark the task as completed
        mockMvc.perform(patch("/tasks/" + taskId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(true)));

        // 5. Get all tasks and verify our task is there
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].id", hasItem(taskId.intValue())));

        // 6. Get tasks by priority
        mockMvc.perform(get("/tasks/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(taskId.intValue())));

        // 7. Get completed tasks
        mockMvc.perform(get("/tasks/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(taskId.intValue())));

        // 8. Update task priority
        mockMvc.perform(patch("/tasks/" + taskId + "/priority/MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority", is("MEDIUM")));

        // 9. Update task due date
        String newDueDate = dateFormat.format(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        mockMvc.perform(patch("/tasks/" + taskId + "/due-date")
                .param("dueDate", newDueDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId.intValue())));

        // 10. Mark task as not completed
        mockMvc.perform(patch("/tasks/" + taskId + "/incomplete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed", is(false)));

        // 11. Delete the task
        mockMvc.perform(delete("/tasks/" + taskId))
                .andExpect(status().isNoContent());

        // 12. Verify task is deleted
        mockMvc.perform(get("/tasks/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testTaskFiltering() throws Exception {
        // Create tasks with different priorities and completion statuses
        createTaskWithPriorityAndStatus("Priority High Task", Task.Priority.HIGH, false);
        createTaskWithPriorityAndStatus("Priority Medium Task", Task.Priority.MEDIUM, false);
        createTaskWithPriorityAndStatus("Priority Low Task", Task.Priority.LOW, true);
        
        // Test filtering by priority
        mockMvc.perform(get("/tasks/priority/HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Priority High Task")));
                
        // Test filtering by completion status
        mockMvc.perform(get("/tasks/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Priority Low Task")));
                
        // Test filtering by priority and status
        mockMvc.perform(get("/tasks/priority/LOW/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Priority Low Task")));
                
        // Test filtering with no results
        mockMvc.perform(get("/tasks/priority/HIGH/status/true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    void testDateBasedFilters() throws Exception {
        // Get today's date
        String today = dateFormat.format(new Date());
        
        // Create tasks with different due dates
        Task task1 = new Task("Today Task", "Due today");
        task1.setDueDate(dateFormat.parse(today));
        
        String tomorrow = dateFormat.format(new Date(System.currentTimeMillis() + 86400000));
        Task task2 = new Task("Tomorrow Task", "Due tomorrow");
        task2.setDueDate(dateFormat.parse(tomorrow));
        
        String yesterday = dateFormat.format(new Date(System.currentTimeMillis() - 86400000));
        Task task3 = new Task("Yesterday Task", "Due yesterday");
        task3.setDueDate(dateFormat.parse(yesterday));
        
        // Save the tasks
        createTask(task1);
        createTask(task2);
        createTask(task3);
        
        // Test getting tasks due today
        mockMvc.perform(get("/tasks/due-date/" + today))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Today Task")));
                
        // Test getting tasks due before tomorrow
        mockMvc.perform(get("/tasks/due-before/" + tomorrow))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", hasItems("Today Task", "Yesterday Task")));
                
        // Test getting tasks due after yesterday
        mockMvc.perform(get("/tasks/due-after/" + yesterday))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", hasItems("Today Task", "Tomorrow Task")));
    }
    
    @Test
    void testErrorHandling() throws Exception {
        // Test invalid task ID
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());
                
        // Test invalid priority value
        mockMvc.perform(get("/tasks/priority/INVALID"))
                .andExpect(status().isBadRequest());
                
        // Test updating non-existent task
        Task nonExistentTask = new Task("Non-existent", "This task doesn't exist");
        nonExistentTask.setId(999L);
        
        mockMvc.perform(put("/tasks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentTask)))
                .andExpect(status().isNotFound());
                
        // Test invalid date format
        mockMvc.perform(get("/tasks/due-date/invalid-date"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testBatchOperations() throws Exception {
        // Create multiple tasks
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Batch Task " + i, "Description " + i);
            task.setPriority(i % 2 == 0 ? Task.Priority.HIGH : Task.Priority.MEDIUM);
            createTask(task);
        }
        
        // Verify all tasks were created
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$[*].title", hasItems(
                    containsString("Batch Task 1"),
                    containsString("Batch Task 2"),
                    containsString("Batch Task 3"),
                    containsString("Batch Task 4"),
                    containsString("Batch Task 5")
                )));
    }
    
    // Helper method to create a task with specific priority and completion status
    private void createTaskWithPriorityAndStatus(String title, Task.Priority priority, boolean completed) throws Exception {
        Task task = new Task(title, "Description for " + title);
        task.setPriority(priority);
        task.setCompleted(completed);
        
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());
    }
    
    // Helper method to create a task
    private void createTask(Task task) throws Exception {
        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());
    }
}
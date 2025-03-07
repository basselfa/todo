// package com.example.todo.todobackend.selenium;

// import java.time.Duration;
// import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.io.InputStream;
// import java.util.List;
// import java.util.logging.LogManager;
// import java.util.logging.Logger;

// import org.junit.jupiter.api.AfterEach;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.openqa.selenium.By;
// import org.openqa.selenium.WebDriver;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.chrome.ChromeOptions;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;

// import io.github.bonigarcia.wdm.WebDriverManager;

// /**
//  * End-to-End tests for the Todo application using Selenium WebDriver.
//  * Tests the integration between frontend and backend by simulating user interactions.
//  */
//  class TodoAppE2ETest {

//     private WebDriver driver;
//     private WebDriverWait wait;
//     private static final String BASE_URL = "https://todobf.netlify.app/";
//     // Set to true by default for CI environments, can be overridden with system property
//     private static final boolean HEADLESS_MODE = Boolean.parseBoolean(
//             System.getProperty("selenium.headless", "true"));
//     // Delay between actions in milliseconds, can be set via system property
//     private static final int ACTION_DELAY = Integer.parseInt(
//             System.getProperty("selenium.delay", "500"));

//     private static final Logger logger = Logger.getLogger(TodoAppE2ETest.class.getName());
    
//     static {
//         try {
//             // Load custom logging configuration
//             InputStream config = TodoAppE2ETest.class.getClassLoader().getResourceAsStream("logging.properties");
//             if (config != null) {
//                 LogManager.getLogManager().readConfiguration(config);
//                 logger.info("Logging configuration loaded");
//             } else {
//                 logger.warning("Could not find logging.properties file");
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }



//     /**
//      * Helper method to add a delay between actions for better visibility
//      */
//     private void slowDown() {
//         if (ACTION_DELAY > 0) {
//             try {
//                 System.out.println("Slowing down execution by " + ACTION_DELAY + "ms...");
//                 Thread.sleep(ACTION_DELAY);
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//             }
//         }
//     }
    
//     @BeforeAll
//      static void setupClass() {
//         // Setup WebDriverManager to automatically download and configure ChromeDriver
//         WebDriverManager.chromedriver().setup();
//         System.out.println("Action delay set to " + Integer.parseInt(System.getProperty("selenium.delay", "2000")) + "ms");
//     }

//     @BeforeEach
//      void setUp() {
//         // Configure Chrome options
//         ChromeOptions options = new ChromeOptions();
        
//         // Only add headless mode if configured to do so
//         if (HEADLESS_MODE) {
//             // Configure Chrome to run in headless mode (no UI)
//             options.addArguments("--headless");
//             System.out.println("Running in headless mode. Set -Dselenium.headless=false to see the browser.");
//         } else {
//             System.out.println("Running with visible browser window.");
//         }
        
//         options.addArguments("--enable-gpu");
//         options.addArguments("--window-size=1920,1080");
//         options.addArguments("--no-sandbox");
//         options.addArguments("--disable-dev-shm-usage");
//         options.addArguments("--remote-allow-origins=*");
        
//         // Initialize the WebDriver using WebDriverManager
//         driver = WebDriverManager.chromedriver().capabilities(options).create();
//         wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
//         // Open the application
//         driver.get(BASE_URL);
//         slowDown(); // Slow down after page load
//     }

//     @AfterEach
//      void tearDown() {
//         if (driver != null) {
//             driver.quit();
//         }
//     }

//     /**
//      * Test the core functionality of the Todo application:
//      * 1. Load the application
//      * 2. Add a new task
//      * 3. Fetch and verify tasks are displayed
//      * 4. Mark a task as completed
//      * 5. Delete a task
//      */
//     @Test
//      void testTodoAppCoreFeatures() {
//         // Verify the page has loaded correctly
//         String pageTitle = driver.getTitle();
//         assertTrue(pageTitle.contains("Todo") || pageTitle.contains("Task"), 
//                 "Page title should contain 'Todo' or 'Task'");
        
//         // Verify the main heading is present
//         WebElement heading = driver.findElement(By.tagName("h1"));
//         assertEquals("Todo List", heading.getText(), "Main heading should be 'Todo List'");
//         slowDown(); // Slow down after verification
        
//         // Create a new task
//         String taskTitle = "Test Task " + System.currentTimeMillis();
//         String taskDescription = "This is a test task created by Selenium";
//         LocalDate tomorrow = LocalDate.now().plusDays(1);
//         String dueDate = "15-03-2026";
        
//         // Fill out the task form
//         WebElement titleInput = driver.findElement(By.xpath("//input[@placeholder='Enter new task']"));
//         titleInput.sendKeys(taskTitle);
//         slowDown(); // Slow down after typing title
        
//         WebElement descriptionInput = driver.findElement(By.xpath("//textarea[@placeholder='Description (optional)']"));
//         descriptionInput.sendKeys(taskDescription);
//         slowDown(); // Slow down after typing description
        
//         WebElement prioritySelect = driver.findElement(By.xpath("//select"));
//         prioritySelect.click();
//         slowDown(); // Slow down after clicking dropdown
        
//         WebElement highPriorityOption = driver.findElement(By.xpath("//option[@value='HIGH']"));
//         highPriorityOption.click();
//         slowDown(); // Slow down after selecting priority
        
//         WebElement dueDateInput = driver.findElement(By.id("due-date"));
//         dueDateInput.sendKeys(dueDate);
//         slowDown(); // Slow down after entering date
        
//         // Submit the form
//         WebElement addButton = driver.findElement(By.xpath("//button[text()='Add Task']"));
//         addButton.click();
//         slowDown(); // Slow down after adding task
  
//         // Click "Fetch Tasks" button to navigate to tasks list
//         WebElement fetchTasksButton = driver.findElement(By.xpath("//button[text()='Fetch Tasks']"));
//         fetchTasksButton.click();
        
//         // Wait for the tasks to load
//         wait.until(ExpectedConditions.urlContains("/tasksShown"));
//         slowDown(); // Slow down after navigation
        
//         // Verify the task was added
//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("task-item")));
//         List<WebElement> tasks = driver.findElements(By.className("task-item"));
//         assertFalse(tasks.isEmpty(), "Task list should not be empty");
//         slowDown(); // Slow down after loading tasks
        
//         // Find our created task
//         boolean taskFound = false;
//         WebElement createdTask = null;
        
//         for (WebElement task : tasks) {
//             WebElement taskTitleElement = task.findElement(By.className("task-title"));
//             // Print to both logger and standard output to ensure visibility
//             // Also try printing stack trace to see where we are
//             new Exception("Debug stack trace for log location").printStackTrace();
//             if (taskTitleElement.getText().equals(taskTitle)) {
//                 taskFound = true;
//                 createdTask = task;
//                 break;
//             }
//         }
        
//         assertTrue(taskFound, "Created task should be found in the task list");
        
//         // Verify task details
//         WebElement taskDescriptionElement = createdTask.findElement(By.className("task-description"));
//         assertEquals(taskDescription, taskDescriptionElement.getText(), "Task description should match");
        
//         WebElement priorityBadge = createdTask.findElement(By.className("priority-badge"));
//         assertEquals("HIGH", priorityBadge.getText(), "Task priority should be HIGH");
        

//         // Mark task as completed
//         WebElement checkbox = createdTask.findElement(By.xpath(".//input[@type='checkbox']"));
//         checkbox.click();
//         slowDown(); // Slow down after marking task as completed
        
//         // Wait for the task to be marked as completed (look for line-through style)
//         wait.until(ExpectedConditions.attributeContains(
//                 createdTask.findElement(By.className("task-title")), 
//                 "style", 
//                 "line-through"));
//         slowDown(); // Slow down after task is marked as completed
        
//         // Edit the task
//         WebElement editButton = createdTask.findElement(By.xpath(".//button[text()='Edit']"));
//         editButton.click();
//         slowDown(); // Slow down after clicking edit
        
//         // Wait for edit form to appear
//         wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("task-edit-form")));
        
//         // Update task title
//         String updatedTitle = taskTitle + " (Updated)";
//         WebElement editTitleInput = createdTask.findElement(By.className("edit-input"));
//         editTitleInput.clear();
//         slowDown(); // Slow down after clearing input
//         editTitleInput.sendKeys(updatedTitle);
//         slowDown(); // Slow down after typing new title
        
//         // Save the changes
//         WebElement saveButton = createdTask.findElement(By.xpath(".//button[text()='Save']"));
//         saveButton.click();
//         slowDown(); // Slow down after saving
        
//         // Verify the task was updated
//         wait.until(ExpectedConditions.textToBe(
//                 By.xpath("//span[contains(@class, 'task-title') and contains(text(), '" + updatedTitle + "')]"),
//                 updatedTitle));
//         slowDown(); // Slow down after verifying update
        
//         // Delete the task
//         // Find the task with the updated title
//         String updatedTaskTitle = taskTitle + " (Updated)";
//         WebElement taskToDelete = findTaskElementByTitle(updatedTaskTitle);
//         // assertNotNull(taskToDelete, "Task with updated title should be found");
        
//         // Find and click the delete button within this specific task
//         WebElement deleteButton = taskToDelete.findElement(By.xpath(".//button[text()='Delete']"));
//         deleteButton.click();
//         logger.info("Deleting task with title: " + updatedTaskTitle);
//         System.out.println("!!! DELETING TASK: " + updatedTaskTitle + " !!!");
//         slowDown(); // Slow down after clicking delete

//         // Verify the task was deleted
//         tasks = driver.findElements(By.className("task-item"));
//         boolean taskStillExists = false;
//         slowDown(); // Slow down after clicking delete
//         logger.info("This is a messasdgdfsgdfsgge");
        
        
//         for (WebElement task : tasks) {
//             try {
//                 WebElement taskTitleElement = task.findElement(By.className("task-title"));
//                 System.out.println("\u001B[31m" +"lkjfösdggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg" +taskTitleElement.getText());
//                 if (taskTitleElement.getText().equals(updatedTitle)) {
//                     taskStillExists = true;
//                     break;
//                 }
//             } catch (Exception e) {
//                 // Element might be stale, continue
//             }
//         }
        
//         assertFalse(taskStillExists, "Task should be deleted from the list");
//         slowDown(); // Slow down after verifying deletion
//     }
    
//     /**
//      * Test task filtering functionality:
//      * 1. Create tasks with different priorities
//      * 2. Filter tasks by priority
//      */
//     @Test
//     public void testTaskFiltering() {
//         // Navigate to the task page
//         driver.get(BASE_URL);
//         slowDown(); // Slow down after navigation
        
//         // Create tasks with different priorities
//         createTask("High Priority Task", "High priority description", "HIGH", LocalDate.now().plusDays(1));
//         slowDown(); // Slow down after creating first task
        
//         createTask("Medium Priority Task", "Medium priority description", "MEDIUM", LocalDate.now().plusDays(2));
//         slowDown(); // Slow down after creating second task
        
//         createTask("Low Priority Task", "Low priority description", "LOW", LocalDate.now().plusDays(3));
//         slowDown(); // Slow down after creating third task
        
//         // Click "Fetch Tasks" button to navigate to tasks list
//         WebElement fetchTasksButton = driver.findElement(By.xpath("//button[text()='Fetch Tasks']"));
//         fetchTasksButton.click();
        
//         // Wait for the tasks to load
//         wait.until(ExpectedConditions.urlContains("/tasksShown"));
//         wait.until(ExpectedConditions.presenceOfElementLocated(By.className("task-item")));
//         slowDown(); // Slow down after loading tasks
        
//         // Verify all three tasks are present
//         List<WebElement> tasks = driver.findElements(By.className("task-item"));
//         assertTrue(tasks.size() >= 3, "At least 3 tasks should be present");
        
//         // Verify we can find our created tasks
//         boolean highFound = findTaskByTitle("High Priority Task");
//         boolean mediumFound = findTaskByTitle("Medium Priority Task");
//         boolean lowFound = findTaskByTitle("Low Priority Task");
        
//         assertTrue(highFound, "High priority task should be found");
//         assertTrue(mediumFound, "Medium priority task should be found");
//         assertTrue(lowFound, "Low priority task should be found");
//         slowDown(); // Slow down after verification
//     }
    
//     /**
//      * Helper method to create a task with specified details
//      */
//     private void createTask(String title, String description, String priority, LocalDate dueDate) {
//         // Fill out the task form
//         WebElement titleInput = driver.findElement(By.xpath("//input[@placeholder='Enter new task']"));
//         titleInput.clear();
//         titleInput.sendKeys(title);
//         slowDown(); // Slow down after entering title
        
//         WebElement descriptionInput = driver.findElement(By.xpath("//textarea[@placeholder='Description (optional)']"));
//         descriptionInput.clear();
//         descriptionInput.sendKeys(description);
//         slowDown(); // Slow down after entering description
        
//         WebElement prioritySelect = driver.findElement(By.xpath("//select"));
//         prioritySelect.click();
//         slowDown(); // Slow down after clicking dropdown
        
//         WebElement priorityOption = driver.findElement(By.xpath("//option[@value='" + priority + "']"));
//         priorityOption.click();
//         slowDown(); // Slow down after selecting priority
        
//         WebElement dueDateInput = driver.findElement(By.id("due-date"));
//         dueDateInput.clear();
//         dueDateInput.sendKeys(dueDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
//         slowDown(); // Slow down after entering date
//         slowDown(); // Slow down after entering date


//         // Submit the form
//         WebElement addButton = driver.findElement(By.xpath("//button[text()='Add Task']"));
//         addButton.click();
//         slowDown(); // Slow down after adding task
//     }
    
//     /**
//      * Helper method to find a task by its title
//      */
//     private boolean findTaskByTitle(String title) {
//         try {
//             List<WebElement> taskTitles = driver.findElements(By.className("task-title"));
//             for (WebElement taskTitle : taskTitles) {
//                 if (taskTitle.getText().equals(title)) {
//                     return true;
//                 }
//             }
//             return false;
//         } catch (Exception e) {
//             return false;
//         }
//     }
//     private WebElement findTaskElementByTitle(String title) {
//         try {
//             List<WebElement> tasks = driver.findElements(By.className("task-item"));
//             for (WebElement task : tasks) {
//                 WebElement taskTitleElement = task.findElement(By.className("task-title"));
//                 logger.info("Checking task title: " + taskTitleElement.getText());
//                 System.out.println("!!! CHECKING TASK: " + taskTitleElement.getText() + " !!!");
//                 if (taskTitleElement.getText().equals(title)) {
//                     logger.info("Found task with title: " + title);
//                     System.out.println("!!! FOUND TASK: " + title + " !!!");
//                     return task;
//                 }
//             }
//             return null;
//         } catch (Exception e) {
//             logger.severe("Error finding task by title: " + e.getMessage());
//             e.printStackTrace();
//             return null;
//         }
//     }
// }
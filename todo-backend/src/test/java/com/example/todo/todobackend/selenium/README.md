# Todo App Selenium Tests

This directory contains end-to-end tests for the Todo application using Selenium WebDriver. These tests verify that the frontend application at https://todobf.netlify.app/ works correctly with the backend API.

## Test Coverage

The tests cover the following functionality:

1. **Core Features Test**
   - Loading the application
   - Creating a new task
   - Fetching and displaying tasks
   - Marking a task as completed
   - Editing a task
   - Deleting a task

2. **Task Filtering Test**
   - Creating tasks with different priorities
   - Verifying tasks are displayed correctly

## Running the Tests

### Prerequisites

- Java JDK 17 or higher
- Maven
- Chrome browser installed (tests run in headless mode by default)

### Commands

To run the Selenium tests:

```bash
# Navigate to the backend project directory
cd todo-backend

# Run the tests
mvn test -Dtest=TodoAppE2ETest
```

### Configuration

The tests are configured to run in headless mode by default. If you want to see the browser UI during test execution, you can use the `selenium.headless` system property:

```bash
# Run tests with visible browser window
mvn test -Dtest=TodoAppE2ETest -Dselenium.headless=false

# Run tests in headless mode (default)
mvn test -Dtest=TodoAppE2ETest -Dselenium.headless=true
```

## Troubleshooting

If the tests fail, check the following:

1. Ensure Chrome browser is installed and up-to-date
2. Verify that the application is accessible at https://todobf.netlify.app/
3. Check that the backend API is running and accessible from the frontend

## Notes

- WebDriverManager is used to automatically download and configure the appropriate ChromeDriver version
- Tests create unique task names using timestamps to avoid conflicts
- Tests clean up after themselves by deleting created tasks
import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import axios from 'axios';
import App from './App';

// Mock axios
jest.mock('axios');

// Mock child components
jest.mock('./TaskPage', () => {
  return function MockTaskPage(props) {
    return (
      <div data-testid="task-page">
        <button data-testid="add-task-button" onClick={() => props.onAddTask({
          title: 'New Task',
          description: 'New Description',
          priority: 'HIGH',
          dueDate: '2025-12-31',
          completed: false
        })}>
          Add Task
        </button>
        <div data-testid="tasks-loaded">{props.tasksLoaded.toString()}</div>
      </div>
    );
  };
});

jest.mock('./TasksShownPage', () => {
  return function MockTasksShownPage(props) {
    return (
      <div data-testid="tasks-shown-page">
        <div data-testid="tasks-count">{props.tasks.length}</div>
        <button data-testid="delete-task-button" onClick={() => props.onDeleteTask(1)}>
          Delete Task
        </button>
        <button data-testid="update-task-button" onClick={() => props.onUpdateTask(1, {
          id: 1,
          title: 'Updated Task',
          description: 'Updated Description',
          priority: 'MEDIUM',
          dueDate: '2025-12-30',
          completed: false
        })}>
          Update Task
        </button>
        <button data-testid="fetch-task-button" onClick={() => props.onFetchTask()}>
          Fetch Tasks
        </button>
        <button data-testid="update-completion-button" onClick={() => props.onUpdateTaskCompletion(1, true)}>
          Complete Task
        </button>
        <button data-testid="update-priority-button" onClick={() => props.onUpdateTaskPriority(1, 'HIGH')}>
          Update Priority
        </button>
        <button data-testid="update-due-date-button" onClick={() => props.onUpdateTaskDueDate(1, '2025-12-31')}>
          Update Due Date
        </button>
      </div>
    );
  };
});

describe('App Component', () => {
  const mockTasks = [
    {
      id: 1,
      title: 'Task 1',
      description: 'Description 1',
      priority: 'HIGH',
      dueDate: '2025-12-31',
      completed: false
    },
    {
      id: 2,
      title: 'Task 2',
      description: 'Description 2',
      priority: 'MEDIUM',
      dueDate: '2025-12-30',
      completed: true
    }
  ];
  
  beforeEach(() => {
    // Reset mocks
    jest.clearAllMocks();
    
    // Mock window.ENV
    window.ENV = { API_URL: 'https://api.example.com/api' };
    
    // Mock location
    delete window.location;
    window.location = { pathname: '/' };
  });
  
  afterEach(() => {
    // Clean up
    delete window.ENV;
  });
  
  test('renders the App with title', () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    expect(screen.getByText('Todo List')).toBeInTheDocument();
  });
  
  test('renders TaskPage on root route', () => {
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    expect(screen.getByTestId('task-page')).toBeInTheDocument();
    expect(screen.getByText('Fetch Tasks')).toBeInTheDocument();
  });
  
  test('fetches tasks when "Fetch Tasks" button is clicked', async () => {
    // Mock axios.get to return tasks
    axios.get.mockResolvedValueOnce({ data: mockTasks });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the fetch tasks button
    const fetchButton = screen.getByText('Fetch Tasks');
    fireEvent.click(fetchButton);
    
    // Check if axios.get was called
    expect(axios.get).toHaveBeenCalledWith('https://api.example.com/api/tasks');
  });
  
  test('adds a new task', async () => {
    // Mock axios.post to return a new task
    const newTask = {
      id: 3,
      title: 'New Task',
      description: 'New Description',
      priority: 'HIGH',
      dueDate: '2025-12-31',
      completed: false
    };
    axios.post.mockResolvedValueOnce({ data: newTask });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the add task button in the mocked TaskPage
    const addTaskButton = screen.getByTestId('add-task-button');
    fireEvent.click(addTaskButton);
    
    // Check if axios.post was called with the correct data
    expect(axios.post).toHaveBeenCalledWith(
      'https://api.example.com/api/tasks',
      expect.objectContaining({
        title: 'New Task',
        description: 'New Description',
        priority: 'HIGH',
        dueDate: '2025-12-31',
        completed: false
      })
    );
  });
  
  test('handles date formatting correctly', async () => {
    // Mock axios.post
    axios.post.mockResolvedValueOnce({ 
      data: { id: 1, title: 'Test', dueDate: '2025-12-31' } 
    });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Get the formatDate function from App component
    // We'll test it indirectly through the handleAddTask function
    
    // Click the add task button in the mocked TaskPage
    const addTaskButton = screen.getByTestId('add-task-button');
    fireEvent.click(addTaskButton);
    
    // Check if axios.post was called with correctly formatted date
    expect(axios.post).toHaveBeenCalledWith(
      'https://api.example.com/api/tasks',
      expect.objectContaining({
        dueDate: '2025-12-31'
      })
    );
  });
  
  test('updates a task', async () => {
    // Mock axios.put to return updated task
    const updatedTask = {
      id: 1,
      title: 'Updated Task',
      description: 'Updated Description',
      priority: 'MEDIUM',
      dueDate: '2025-12-30',
      completed: false
    };
    axios.put.mockResolvedValueOnce({ data: updatedTask });
    
    // Set up the component in the tasks shown route
    window.location.pathname = '/tasksShown';
    axios.get.mockResolvedValueOnce({ data: mockTasks });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Wait for tasks to load
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
    
    // Render the TasksShownPage with tasks
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the update task button in the mocked TasksShownPage
    const updateTaskButton = screen.getByTestId('update-task-button');
    fireEvent.click(updateTaskButton);
    
    // Check if axios.put was called with the correct data
    expect(axios.put).toHaveBeenCalledWith(
      'https://api.example.com/api/tasks/1',
      expect.objectContaining({
        title: 'Updated Task',
        description: 'Updated Description',
        priority: 'MEDIUM',
        dueDate: '2025-12-30',
        completed: false
      })
    );
  });
  
  test('deletes a task', async () => {
    // Mock axios.delete
    axios.delete.mockResolvedValueOnce({});
    
    // Set up the component in the tasks shown route
    window.location.pathname = '/tasksShown';
    axios.get.mockResolvedValueOnce({ data: mockTasks });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Wait for tasks to load
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
    
    // Render the TasksShownPage with tasks
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the delete task button in the mocked TasksShownPage
    const deleteTaskButton = screen.getByTestId('delete-task-button');
    fireEvent.click(deleteTaskButton);
    
    // Check if axios.delete was called with the correct id
    expect(axios.delete).toHaveBeenCalledWith('https://api.example.com/api/tasks/1');
  });
  
  test('updates task completion status', async () => {
    // Mock axios.patch to return updated task
    const completedTask = {
      id: 1,
      title: 'Task 1',
      description: 'Description 1',
      priority: 'HIGH',
      dueDate: '2025-12-31',
      completed: true
    };
    axios.patch.mockResolvedValueOnce({ data: completedTask });
    
    // Set up the component in the tasks shown route
    window.location.pathname = '/tasksShown';
    axios.get.mockResolvedValueOnce({ data: mockTasks });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Wait for tasks to load
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
    
    // Render the TasksShownPage with tasks
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the update completion button in the mocked TasksShownPage
    const updateCompletionButton = screen.getByTestId('update-completion-button');
    fireEvent.click(updateCompletionButton);
    
    // Check if axios.patch was called with the correct endpoint
    expect(axios.patch).toHaveBeenCalledWith('https://api.example.com/api/tasks/1/complete');
  });
  
  test('updates task priority', async () => {
    // Mock axios.patch to return updated task
    const updatedTask = {
      id: 1,
      title: 'Task 1',
      description: 'Description 1',
      priority: 'HIGH',
      dueDate: '2025-12-31',
      completed: false
    };
    axios.patch.mockResolvedValueOnce({ data: updatedTask });
    
    // Set up the component in the tasks shown route
    window.location.pathname = '/tasksShown';
    axios.get.mockResolvedValueOnce({ data: mockTasks });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Wait for tasks to load
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
    
    // Render the TasksShownPage with tasks
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the update priority button in the mocked TasksShownPage
    const updatePriorityButton = screen.getByTestId('update-priority-button');
    fireEvent.click(updatePriorityButton);
    
    // Check if axios.patch was called with the correct endpoint and priority
    expect(axios.patch).toHaveBeenCalledWith('https://api.example.com/api/tasks/1/priority/HIGH');
  });
  
  test('updates task due date', async () => {
    // Mock axios.patch to return updated task
    const updatedTask = {
      id: 1,
      title: 'Task 1',
      description: 'Description 1',
      priority: 'HIGH',
      dueDate: '2025-12-31',
      completed: false
    };
    axios.patch.mockResolvedValueOnce({ data: updatedTask });
    
    // Set up the component in the tasks shown route
    window.location.pathname = '/tasksShown';
    axios.get.mockResolvedValueOnce({ data: mockTasks });
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Wait for tasks to load
    await waitFor(() => {
      expect(axios.get).toHaveBeenCalled();
    });
    
    // Render the TasksShownPage with tasks
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Click the update due date button in the mocked TasksShownPage
    const updateDueDateButton = screen.getByTestId('update-due-date-button');
    fireEvent.click(updateDueDateButton);
    
    // Check if axios.patch was called with the correct endpoint and due date
    expect(axios.patch).toHaveBeenCalledWith(
      'https://api.example.com/api/tasks/1/due-date',
      { dueDate: '2025-12-31' }
    );
  });
  
  test('handles API errors gracefully', async () => {
    // Mock console.error
    const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation();
    
    // Mock axios.get to throw an error
    axios.get.mockRejectedValueOnce(new Error('API error'));
    
    // Set up the component in the tasks shown route
    window.location.pathname = '/tasksShown';
    
    render(
      <BrowserRouter>
        <App />
      </BrowserRouter>
    );
    
    // Wait for error to be logged
    await waitFor(() => {
      expect(consoleErrorMock).toHaveBeenCalledWith(
        'Error fetching tasks:',
        expect.any(Error)
      );
    });
    
    consoleErrorMock.mockRestore();
  });
});
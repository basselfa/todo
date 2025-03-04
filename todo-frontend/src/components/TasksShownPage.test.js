import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { Router } from 'react-router-dom';
import { createMemoryHistory } from 'history';
import TasksShownPage from './TasksShownPage';

// Mock TaskList component
jest.mock('./TaskList', () => {
  return function MockTaskList({ tasks }) {
    return (
      <div data-testid="task-list">
        {tasks.map(task => (
          <div key={task.id} data-testid={`task-item-${task.id}`}>
            {task.title}
          </div>
        ))}
      </div>
    );
  };
});

describe('TasksShownPage Component', () => {
  // Mock handlers
  const mockDeleteTask = jest.fn();
  const mockUpdateTask = jest.fn();
  const mockFetchTask = jest.fn();
  const mockUpdateTaskCompletion = jest.fn();
  const mockUpdateTaskPriority = jest.fn();
  const mockUpdateTaskDueDate = jest.fn();
  
  // Mock tasks
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
  
  // Setup history for router
  const history = createMemoryHistory();
  
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  test('renders task overview title', () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Check if the title is rendered
    expect(screen.getByText('Task Overview')).toBeInTheDocument();
  });
  
  test('renders filter controls', () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Check if filter controls are rendered
    expect(screen.getByText('Filter Tasks')).toBeInTheDocument();
    expect(screen.getByLabelText('Priority:')).toBeInTheDocument();
    expect(screen.getByLabelText('Status:')).toBeInTheDocument();
    expect(screen.getByLabelText('Date Filter:')).toBeInTheDocument();
    
    expect(screen.getByRole('button', { name: /apply filters/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /clear filters/i })).toBeInTheDocument();
  });
  
  test('renders TaskList with tasks', () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Check if TaskList is rendered with tasks
    expect(screen.getByTestId('task-list')).toBeInTheDocument();
    expect(screen.getByTestId('task-item-1')).toBeInTheDocument();
    expect(screen.getByTestId('task-item-2')).toBeInTheDocument();
  });
  
  test('displays "No tasks found" when tasks array is empty', () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={[]}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Check if the no tasks message is displayed
    expect(screen.getByText('No tasks found.')).toBeInTheDocument();
  });
  
  test('navigates back when back button is clicked', async () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Click the back button
    const backButton = screen.getByRole('button', { name: /back to main page/i });
    fireEvent.click(backButton);
    
    // Check if navigation was triggered
    expect(history.location.pathname).toBe('/');
  });
  
  test('applies filters when apply button is clicked', async () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Set priority filter
    const prioritySelect = screen.getByLabelText('Priority:');
    fireEvent.change(prioritySelect, { target: { value: 'HIGH' } });
    
    // Set status filter
    const statusSelect = screen.getByLabelText('Status:');
    fireEvent.change(statusSelect, { target: { value: 'true' } });
    
    // Click apply filters button
    const applyButton = screen.getByRole('button', { name: /apply filters/i });
    fireEvent.click(applyButton);
    
    // Check if fetchTask was called with the correct filters
    expect(mockFetchTask).toHaveBeenCalledWith({
      priority: 'HIGH',
      completed: true
    });
  });
  
  test('clears filters when clear button is clicked', async () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Set some filters first
    const prioritySelect = screen.getByLabelText('Priority:');
    fireEvent.change(prioritySelect, { target: { value: 'HIGH' } });
    
    // Click clear filters button
    const clearButton = screen.getByRole('button', { name: /clear filters/i });
    fireEvent.click(clearButton);
    
    // Check if filters were reset
    expect(prioritySelect).toHaveValue('');
    
    // Check if fetchTask was called with no filters
    expect(mockFetchTask).toHaveBeenCalledWith();
  });
  
  test('shows date input when date filter is selected', async () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Date input should not be visible initially
    expect(screen.queryByLabelText('Date:')).not.toBeInTheDocument();
    
    // Select a date filter
    const dateFilterSelect = screen.getByLabelText('Date Filter:');
    fireEvent.change(dateFilterSelect, { target: { value: 'dueDate' } });
    
    // Date input should now be visible
    expect(screen.getByLabelText('Date:')).toBeInTheDocument();
  });
  
  test('applies date filter correctly', async () => {
    render(
      <Router history={history}>
        <TasksShownPage 
          tasks={mockTasks}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      </Router>
    );
    
    // Select a date filter
    const dateFilterSelect = screen.getByLabelText('Date Filter:');
    fireEvent.change(dateFilterSelect, { target: { value: 'dueDate' } });
    
    // Set date value
    const dateInput = screen.getByLabelText('Date:');
    fireEvent.change(dateInput, { target: { value: '2025-12-31' } });
    
    // Click apply filters button
    const applyButton = screen.getByRole('button', { name: /apply filters/i });
    fireEvent.click(applyButton);
    
    // Check if fetchTask was called with the correct filter
    expect(mockFetchTask).toHaveBeenCalledWith({
      dueDate: '2025-12-31'
    });
  });
});
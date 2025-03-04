import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import TaskItem from './TaskItem';

describe('TaskItem Component', () => {
  // Mock handlers
  const mockDeleteTask = jest.fn();
  const mockUpdateTask = jest.fn();
  const mockFetchTask = jest.fn();
  const mockUpdateTaskCompletion = jest.fn();
  const mockUpdateTaskPriority = jest.fn();
  const mockUpdateTaskDueDate = jest.fn();
  
  // Mock task
  const mockTask = {
    id: 1,
    title: 'Test Task',
    description: 'Test Description',
    priority: 'MEDIUM',
    dueDate: '2025-12-31',
    completed: false
  };
  
  beforeEach(() => {
    jest.clearAllMocks();
  });
  
  test('renders task details correctly', () => {
    render(
      <TaskItem
        task={mockTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Check if task details are displayed
    expect(screen.getByText('Test Task')).toBeInTheDocument();
    expect(screen.getByText('Test Description')).toBeInTheDocument();
    expect(screen.getByText('MEDIUM')).toBeInTheDocument();
    expect(screen.getByText('2025-12-31')).toBeInTheDocument();
    
    // Check if buttons are present
    expect(screen.getByRole('button', { name: /edit/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /delete/i })).toBeInTheDocument();
    
    // Check if checkbox is unchecked
    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).not.toBeChecked();
  });
  
  test('renders completed task with appropriate styling', () => {
    const completedTask = { ...mockTask, completed: true };
    
    render(
      <TaskItem
        task={completedTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Check if checkbox is checked
    const checkbox = screen.getByRole('checkbox');
    expect(checkbox).toBeChecked();
    
    // Check if title has line-through style
    const taskTitle = screen.getByText('Test Task');
    expect(taskTitle).toHaveStyle('text-decoration: line-through');
  });
  
  test('toggles completion status when checkbox is clicked', async () => {
    mockUpdateTaskCompletion.mockResolvedValueOnce({ ...mockTask, completed: true });
    
    render(
      <TaskItem
        task={mockTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Click the checkbox
    const checkbox = screen.getByRole('checkbox');
    fireEvent.click(checkbox);
    
    // Check if completion status update was called
    expect(mockUpdateTaskCompletion).toHaveBeenCalledWith(1, true);
  });
  
  test('deletes task when delete button is clicked', async () => {
    render(
      <TaskItem
        task={mockTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Click the delete button
    const deleteButton = screen.getByRole('button', { name: /delete/i });
    fireEvent.click(deleteButton);
    
    // Check if delete task was called
    expect(mockDeleteTask).toHaveBeenCalledWith(1);
  });
  
  test('switches to edit mode when edit button is clicked', async () => {
    render(
      <TaskItem
        task={mockTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Click the edit button
    const editButton = screen.getByRole('button', { name: /edit/i });
    fireEvent.click(editButton);
    
    // Check if form inputs are displayed
    expect(screen.getByDisplayValue('Test Task')).toBeInTheDocument();
    expect(screen.getByDisplayValue('Test Description')).toBeInTheDocument();
    expect(screen.getByDisplayValue('2025-12-31')).toBeInTheDocument();
    
    // Check if save and cancel buttons are present
    expect(screen.getByRole('button', { name: /save/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /cancel/i })).toBeInTheDocument();
  });
  
  test('updates task when save button is clicked', async () => {
    mockUpdateTask.mockResolvedValueOnce({
      ...mockTask,
      title: 'Updated Task',
      description: 'Updated Description',
      priority: 'HIGH',
      dueDate: '2026-01-01'
    });
    
    render(
      <TaskItem
        task={mockTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Click the edit button
    const editButton = screen.getByRole('button', { name: /edit/i });
    fireEvent.click(editButton);
    
    // Update form inputs
    const titleInput = screen.getByDisplayValue('Test Task');
    const descriptionInput = screen.getByDisplayValue('Test Description');
    const prioritySelect = screen.getByRole('combobox');
    const dateInput = screen.getByDisplayValue('2025-12-31');
    
    fireEvent.change(titleInput, { target: { value: 'Updated Task' } });
    fireEvent.change(descriptionInput, { target: { value: 'Updated Description' } });
    fireEvent.change(prioritySelect, { target: { value: 'HIGH' } });
    fireEvent.change(dateInput, { target: { value: '2026-01-01' } });
    
    // Click the save button
    const saveButton = screen.getByRole('button', { name: /save/i });
    fireEvent.click(saveButton);
    
    // Check if update task was called with correct values
    expect(mockUpdateTask).toHaveBeenCalledWith(1, {
      id: 1,
      title: 'Updated Task',
      description: 'Updated Description',
      priority: 'HIGH',
      dueDate: '2026-01-01',
      completed: false
    });
  });
  
  test('exits edit mode when cancel button is clicked', async () => {
    render(
      <TaskItem
        task={mockTask}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Click the edit button to enter edit mode
    const editButton = screen.getByRole('button', { name: /edit/i });
    fireEvent.click(editButton);
    
    // Click the cancel button
    const cancelButton = screen.getByRole('button', { name: /cancel/i });
    fireEvent.click(cancelButton);
    
    // Check if we're back to view mode
    expect(screen.getByText('Test Task')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /edit/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /delete/i })).toBeInTheDocument();
  });
  
  test('displays the correct priority color', () => {
    // Test with different priorities
    const priorities = [
      { value: 'HIGH', expectedColor: '#f44336' },
      { value: 'MEDIUM', expectedColor: '#ff9800' },
      { value: 'LOW', expectedColor: '#4caf50' }
    ];
    
    priorities.forEach(({ value, expectedColor }) => {
      const priorityTask = { ...mockTask, priority: value };
      
      const { container, unmount } = render(
        <TaskItem
          task={priorityTask}
          onDeleteTask={mockDeleteTask}
          onUpdateTask={mockUpdateTask}
          onFetchTask={mockFetchTask}
          onUpdateTaskCompletion={mockUpdateTaskCompletion}
          onUpdateTaskPriority={mockUpdateTaskPriority}
          onUpdateTaskDueDate={mockUpdateTaskDueDate}
        />
      );
      
      const priorityBadge = container.querySelector('.priority-badge');
      expect(priorityBadge).toHaveStyle(`background-color: ${expectedColor}`);
      
      unmount();
    });
  });
});
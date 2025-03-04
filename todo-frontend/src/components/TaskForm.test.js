import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import TaskForm from './TaskForm';

describe('TaskForm Component', () => {
  const mockAddTask = jest.fn();
  
  beforeEach(() => {
    mockAddTask.mockClear();
  });
  
  test('renders form elements correctly', () => {
    render(<TaskForm onAddTask={mockAddTask} />);
    
    // Check if all form elements are present
    expect(screen.getByPlaceholderText('Enter new task')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Description (optional)')).toBeInTheDocument();
    expect(screen.getByRole('combobox')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /add task/i })).toBeInTheDocument();
    
    // Check if the priority dropdown has the correct options
    const priorityOptions = screen.getAllByRole('option');
    expect(priorityOptions).toHaveLength(3);
    expect(priorityOptions[0]).toHaveTextContent('Low');
    expect(priorityOptions[1]).toHaveTextContent('Medium');
    expect(priorityOptions[2]).toHaveTextContent('High');
  });
  
  test('default form values are set correctly', () => {
    render(<TaskForm onAddTask={mockAddTask} />);
    
    // Title and description should be empty by default
    expect(screen.getByPlaceholderText('Enter new task')).toHaveValue('');
    expect(screen.getByPlaceholderText('Description (optional)')).toHaveValue('');
    
    // Priority should be MEDIUM by default
    expect(screen.getByRole('option', { name: 'Medium' }).selected).toBe(true);
  });
  
  test('submits form with correct values', () => {
    render(<TaskForm onAddTask={mockAddTask} />);
    
    // Fill out the form
    const titleInput = screen.getByPlaceholderText('Enter new task');
    const descriptionInput = screen.getByPlaceholderText('Description (optional)');
    const prioritySelect = screen.getByRole('combobox');
    const dateInput = screen.getByLabelText('Due date') || screen.getByRole('textbox', { type: 'date' });
    const submitButton = screen.getByRole('button', { name: /add task/i });
    
    // Use fireEvent instead of userEvent
    fireEvent.change(titleInput, { target: { value: 'Test Task' } });
    fireEvent.change(descriptionInput, { target: { value: 'Test Description' } });
    fireEvent.change(prioritySelect, { target: { value: 'HIGH' } });
    
    // Set date (YYYY-MM-DD format)
    const testDate = '2025-12-31';
    fireEvent.change(dateInput, { target: { value: testDate } });
    
    // Submit the form
    fireEvent.click(submitButton);
    
    // Check if onAddTask was called with the correct values
    expect(mockAddTask).toHaveBeenCalledTimes(1);
    expect(mockAddTask).toHaveBeenCalledWith({
      title: 'Test Task',
      description: 'Test Description',
      priority: 'HIGH',
      dueDate: testDate,
      completed: false
    });
  });
  
  test('does not submit when required fields are missing', () => {
    // Mock window.alert
    const alertMock = jest.spyOn(window, 'alert').mockImplementation();
    
    render(<TaskForm onAddTask={mockAddTask} />);
    
    // Submit without filling required fields
    const submitButton = screen.getByRole('button', { name: /add task/i });
    fireEvent.click(submitButton);
    
    // Check if alert was shown
    expect(alertMock).toHaveBeenCalledWith('Please fill in all required fields');
    expect(mockAddTask).not.toHaveBeenCalled();
    
    alertMock.mockRestore();
  });
  
  test('resets form after successful submission', async () => {
    // Mock successful task addition
    mockAddTask.mockResolvedValueOnce({ id: 1 });
    
    render(<TaskForm onAddTask={mockAddTask} />);
    
    // Fill out the form
    const titleInput = screen.getByPlaceholderText('Enter new task');
    const descriptionInput = screen.getByPlaceholderText('Description (optional)');
    const prioritySelect = screen.getByRole('combobox');
    const dateInput = screen.getByRole('textbox', { type: 'date' });
    const submitButton = screen.getByRole('button', { name: /add task/i });
    
    fireEvent.change(titleInput, { target: { value: 'Test Task' } });
    fireEvent.change(descriptionInput, { target: { value: 'Test Description' } });
    fireEvent.change(prioritySelect, { target: { value: 'HIGH' } });
    
    const testDate = '2025-12-31';
    fireEvent.change(dateInput, { target: { value: testDate } });
    
    // Submit the form
    fireEvent.click(submitButton);
    
    // Wait for the async operation to complete
    await new Promise(resolve => setTimeout(resolve, 0));
    
    // Check if form was reset
    expect(titleInput).toHaveValue('');
    expect(descriptionInput).toHaveValue('');
    expect(screen.getByRole('option', { name: 'Medium' }).selected).toBe(true);
    expect(dateInput).toHaveValue('');
  });
  
  test('shows error when task addition fails', async () => {
    // Mock window.alert
    const alertMock = jest.spyOn(window, 'alert').mockImplementation();
    const consoleMock = jest.spyOn(console, 'error').mockImplementation();
    
    // Mock failed task addition
    const error = new Error('API error');
    mockAddTask.mockRejectedValueOnce(error);
    
    render(<TaskForm onAddTask={mockAddTask} />);
    
    // Fill out the form with minimum required fields
    const titleInput = screen.getByPlaceholderText('Enter new task');
    const dateInput = screen.getByRole('textbox', { type: 'date' });
    const submitButton = screen.getByRole('button', { name: /add task/i });
    
    fireEvent.change(titleInput, { target: { value: 'Test Task' } });
    
    const testDate = '2025-12-31';
    fireEvent.change(dateInput, { target: { value: testDate } });
    
    // Submit the form
    fireEvent.click(submitButton);
    
    // Wait for the async operation to complete
    await new Promise(resolve => setTimeout(resolve, 0));
    
    // Check if error was logged and alert was shown
    expect(consoleMock).toHaveBeenCalledWith('Error adding task:', error);
    expect(alertMock).toHaveBeenCalledWith('Error adding task');
    
    alertMock.mockRestore();
    consoleMock.mockRestore();
  });
});
import React from 'react';
import { render, screen } from '@testing-library/react';
import TasksShown from './TasksShown';

describe('TasksShown Component', () => {
  test('renders component title', () => {
    render(<TasksShown tasks={[]} />);
    
    // Check if the title is rendered
    expect(screen.getByText('Tasks Shown')).toBeInTheDocument();
  });
  
  test('displays "No tasks found" message when tasks array is empty', () => {
    render(<TasksShown tasks={[]} />);
    
    // Check if the no tasks message is displayed
    expect(screen.getByText('No tasks found.')).toBeInTheDocument();
  });
  
  test('renders list of tasks when tasks are provided', () => {
    const mockTasks = [
      { id: 1, name: 'Task 1' },
      { id: 2, name: 'Task 2' },
      { id: 3, name: 'Task 3' }
    ];
    
    render(<TasksShown tasks={mockTasks} />);
    
    // Check if all tasks are rendered
    expect(screen.getByText('Task 1')).toBeInTheDocument();
    expect(screen.getByText('Task 2')).toBeInTheDocument();
    expect(screen.getByText('Task 3')).toBeInTheDocument();
    
    // Check if list items are created
    const listItems = screen.getAllByRole('listitem');
    expect(listItems).toHaveLength(3);
  });
});
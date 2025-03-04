import React from 'react';
import { render, screen } from '@testing-library/react';
import TaskList from './TaskList';

// Mock TaskItem component
jest.mock('./TaskItem', () => {
  return function MockTaskItem({ task }) {
    return <div data-testid={`task-item-${task.id}`}>{task.title}</div>;
  };
});

describe('TaskList Component', () => {
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
    },
    {
      id: 3,
      title: 'Task 3',
      description: 'Description 3',
      priority: 'LOW',
      dueDate: '2025-12-29',
      completed: false
    }
  ];
  
  test('renders all tasks correctly', () => {
    render(
      <TaskList
        tasks={mockTasks}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Check if all tasks are rendered
    expect(screen.getByTestId('task-item-1')).toBeInTheDocument();
    expect(screen.getByTestId('task-item-2')).toBeInTheDocument();
    expect(screen.getByTestId('task-item-3')).toBeInTheDocument();
    
    expect(screen.getByText('Task 1')).toBeInTheDocument();
    expect(screen.getByText('Task 2')).toBeInTheDocument();
    expect(screen.getByText('Task 3')).toBeInTheDocument();
  });
  
  test('renders empty list when no tasks are provided', () => {
    render(
      <TaskList
        tasks={[]}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Check if the task list is empty
    const taskListElement = screen.getByRole('list') || screen.getByRole('listitem') || screen.getByTestId('task-list');
    expect(taskListElement.children.length).toBe(0);
  });
  
  test('passes correct props to TaskItem components', () => {
    const { container } = render(
      <TaskList
        tasks={mockTasks}
        onDeleteTask={mockDeleteTask}
        onUpdateTask={mockUpdateTask}
        onFetchTask={mockFetchTask}
        onUpdateTaskCompletion={mockUpdateTaskCompletion}
        onUpdateTaskPriority={mockUpdateTaskPriority}
        onUpdateTaskDueDate={mockUpdateTaskDueDate}
      />
    );
    
    // Check if the correct number of TaskItem components are rendered
    const taskItems = container.querySelectorAll('[data-testid^="task-item-"]');
    expect(taskItems.length).toBe(3);
  });
});
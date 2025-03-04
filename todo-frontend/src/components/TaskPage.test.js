import React from 'react';
import { render, screen } from '@testing-library/react';
import TaskPage from './TaskPage';

// Mock TaskForm component
jest.mock('./TaskForm', () => {
  return function MockTaskForm(props) {
    return <div data-testid="task-form" onClick={props.onAddTask}>Mock Task Form</div>;
  };
});

describe('TaskPage Component', () => {
  const mockAddTask = jest.fn();
  
  test('renders task page with title', () => {
    render(<TaskPage onAddTask={mockAddTask} tasksLoaded={false} />);
    
    // Check if the title is rendered
    expect(screen.getByText('Tasks')).toBeInTheDocument();
  });
  
  test('renders TaskForm component', () => {
    render(<TaskPage onAddTask={mockAddTask} tasksLoaded={false} />);
    
    // Check if TaskForm is rendered
    expect(screen.getByTestId('task-form')).toBeInTheDocument();
    expect(screen.getByText('Mock Task Form')).toBeInTheDocument();
  });
  
  test('passes onAddTask prop to TaskForm', () => {
    render(<TaskPage onAddTask={mockAddTask} tasksLoaded={false} />);
    
    // Click on the mock TaskForm to trigger onAddTask
    screen.getByTestId('task-form').click();
    
    // Check if onAddTask was called
    expect(mockAddTask).toHaveBeenCalled();
  });
  
  test('displays loading message when tasks are not loaded', () => {
    render(<TaskPage onAddTask={mockAddTask} tasksLoaded={false} />);
    
    // Check if loading message is displayed
    expect(screen.getByText("Click 'Fetch Tasks' to load tasks")).toBeInTheDocument();
  });
  
  test('does not display loading message when tasks are loaded', () => {
    render(<TaskPage onAddTask={mockAddTask} tasksLoaded={true} />);
    
    // Check if loading message is not displayed
    expect(screen.queryByText("Click 'Fetch Tasks' to load tasks")).not.toBeInTheDocument();
  });
});
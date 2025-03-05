import React, { useState, useCallback, useEffect } from 'react';
import axios from 'axios';
import { Route, Switch, useHistory } from 'react-router-dom';
import TaskPage from './TaskPage';
import TasksShownPage from './TasksShownPage';
import '../App.css';
const App = () => {
const [tasks, setTasks] = useState([]);
const [tasksLoaded, setTasksLoaded] = useState(false);
const [loading, setLoading] = useState(false);
const history = useHistory();

// Format date to yyyy-MM-dd
const formatDate = (dateString) => {
  if (!dateString) return null;
  const date = new Date(dateString);
  return date.toISOString().split('T')[0]; // Returns yyyy-MM-dd
};

// GET /tasks - Fetch all tasks
const fetchTasks = useCallback(async (filters = {}) => {
  setLoading(true);
  try {
    let url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks`;
    
    // Apply filters if provided
    if (filters.priority && filters.completed !== undefined) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/priority/${filters.priority}/status/${filters.completed}`;
    } else if (filters.priority && filters.dueDate) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/priority/${filters.priority}/due-date/${formatDate(filters.dueDate)}`;
    } else if (filters.priority) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/priority/${filters.priority}`;
    } else if (filters.completed !== undefined) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/status/${filters.completed}`;
    } else if (filters.dueDate) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/due-date/${formatDate(filters.dueDate)}`;
    } else if (filters.dueBefore) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/due-before/${formatDate(filters.dueBefore)}`;
    } else if (filters.dueAfter) {
      url = `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/due-after/${formatDate(filters.dueAfter)}`;
    }
    
    const response = await axios.get(url);
    setTasks(response.data);
    setTasksLoaded(true);
  } catch (error) {
    console.error('Error fetching tasks:', error);
  } finally {
    setLoading(false);
  }
}, []);

// Add effect to fetch tasks when component mounts
useEffect(() => {
  // Only fetch tasks if we're on the tasks page
  if (window.location.pathname.includes('/tasksShown')) {
    fetchTasks(); // Fetch all tasks with no filters initially
  }
}, [fetchTasks]);

// GET /tasks/{id} - Fetch a single task
const fetchTask = async (id) => {
  try {
    const response = await axios.get(`${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching task ${id}:`, error);
    return null;
  }
};

// POST /tasks - Create a new task
const handleAddTask = async (task) => {
  try {
    // Ensure date is formatted correctly
    if (task.dueDate) {
      task.dueDate = formatDate(task.dueDate);
    }
    const response = await axios.post(`${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks`, task);
    setTasks(prevTasks => [...prevTasks, response.data]);
    return response.data;
  } catch (error) {
    console.error('Error adding task:', error);
    return null;
  }
};

// PUT /tasks/{id} - Update a task
const handleUpdateTask = async (id, updatedTask) => {
  try {
    // Ensure date is formatted correctly
    if (updatedTask.dueDate) {
      updatedTask.dueDate = formatDate(updatedTask.dueDate);
    }
    const response = await axios.put(`${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}`, updatedTask);
    setTasks(prevTasks => prevTasks.map(task => (task.id === id ? response.data : task)));
    return response.data;
  } catch (error) {
    console.error('Error updating task:', error);
    return null;
  }
};

// DELETE /tasks/{id} - Delete a task
const handleDeleteTask = async (id) => {
  try {
    await axios.delete(`${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}`);
    setTasks(prevTasks => prevTasks.filter(task => task.id !== id));
    return true;
  } catch (error) {
    console.error('Error deleting task:', error);
    return false;
  }
};

// PATCH /tasks/{id}/complete - Update task completion status
const handleUpdateTaskCompletion = async (id, isCompleted) => {
  try {
    let response;
    if (isCompleted) {
      response = await axios.patch(
        `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}/complete`
      );
    } else {
      response = await axios.patch(
        `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}/incomplete`
      );
    }
    setTasks(prevTasks => prevTasks.map(task => (task.id === id ? response.data : task)));
    return response.data;
  } catch (error) {
    console.error(`Error updating completion status for task ${id}:`, error);
    return null;
  }
};

// PATCH /tasks/{id}/priority/{priority} - Update task priority
const handleUpdateTaskPriority = async (id, priority) => {
  try {
    // Ensure priority is one of the valid enum values: LOW, MEDIUM, HIGH
    if (!['LOW', 'MEDIUM', 'HIGH'].includes(priority)) {
      console.error('Invalid priority value. Must be LOW, MEDIUM, or HIGH');
      return null;
    }
    
    const response = await axios.patch(
      `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}/priority/${priority}`
    );
    setTasks(prevTasks => prevTasks.map(task => (task.id === id ? response.data : task)));
    return response.data;
  } catch (error) {
    console.error(`Error updating priority for task ${id}:`, error);
    return null;
  }
};

// PATCH /tasks/{id}/due-date - Update task due date
const handleUpdateTaskDueDate = async (id, dueDate) => {
  try {
    // Format date as yyyy-MM-dd
    const formattedDate = formatDate(dueDate);
    
    const response = await axios.patch(
      `${window.ENV?.API_URL || process.env.REACT_APP_API_URL}/tasks/${id}/due-date`,
      { dueDate: formattedDate }
    );
    setTasks(prevTasks => prevTasks.map(task => (task.id === id ? response.data : task)));
    return response.data;
  } catch (error) {
    console.error(`Error updating due date for task ${id}:`, error);
    return null;
  }
};
  return (
    <div className="App">
      <h1>Todo List</h1>
      <Switch>
        <Route exact path="/">
          <button onClick={() => {
            fetchTasks();
            history.push('/tasksShown');
          }} className="button">
            Fetch Tasks
          </button>
          <TaskPage 
            onAddTask={handleAddTask}
            tasksLoaded={tasksLoaded}
          />
        </Route>
        <Route path="/tasksShown">
          <TasksShownPage 
            tasks={tasks} 
            onDeleteTask={handleDeleteTask} 
            onUpdateTask={handleUpdateTask}
            onFetchTask={fetchTasks} 
            onUpdateTaskCompletion={handleUpdateTaskCompletion}
            onUpdateTaskPriority={handleUpdateTaskPriority}
            onUpdateTaskDueDate={handleUpdateTaskDueDate}
          />
        </Route>
      </Switch>
    </div>
  );
};
export default App;
import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import TaskList from './TaskList';
import './TasksShownPage.css';

const TasksShownPage = ({ 
  tasks, 
  onDeleteTask, 
  onUpdateTask,
  onFetchTask,
  onUpdateTaskCompletion,
  onUpdateTaskPriority,
  onUpdateTaskDueDate
}) => {
  const history = useHistory();
  const [filters, setFilters] = useState({
    priority: '',
    completed: '',
    dateFilter: '',
    dateValue: ''
  });
  
  const handleBackClick = () => {
    history.push('/');
  };
  
  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };
  
  const applyFilters = () => {
    const apiFilters = {};
    
    if (filters.priority) {
      apiFilters.priority = filters.priority;
    }
    
    if (filters.completed !== '') {
      apiFilters.completed = filters.completed === 'true';
    }
    
    if (filters.dateFilter && filters.dateValue) {
      switch(filters.dateFilter) {
        case 'dueDate':
          apiFilters.dueDate = filters.dateValue;
          break;
        case 'dueBefore':
          apiFilters.dueBefore = filters.dateValue;
          break;
        case 'dueAfter':
          apiFilters.dueAfter = filters.dateValue;
          break;
        default:
          break;
      }
    }
    
    onFetchTask(apiFilters);
  };
  
  const clearFilters = () => {
    setFilters({
      priority: '',
      completed: '',
      dateFilter: '',
      dateValue: ''
    });
    onFetchTask(); // Fetch all tasks with no filters
  };

  return (
    <div className="tasks-shown-container">
      <h2>Task Overview</h2>
      <div className="button-container">
        <button className="back-button" onClick={handleBackClick}>
          Back to Main Page
        </button>
      </div>
      
      <div className="filters-container">
        <h3>Filter Tasks</h3>
        <div className="filter-row">
          <div className="filter-group">
            <label htmlFor="priority">Priority:</label>
            <select 
              id="priority" 
              name="priority" 
              value={filters.priority} 
              onChange={handleFilterChange}
            >
              <option value="">All Priorities</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
          
          <div className="filter-group">
            <label htmlFor="completed">Status:</label>
            <select 
              id="completed" 
              name="completed" 
              value={filters.completed} 
              onChange={handleFilterChange}
            >
              <option value="">All Tasks</option>
              <option value="true">Completed</option>
              <option value="false">Not Completed</option>
            </select>
          </div>
        </div>
        
        <div className="filter-row">
          <div className="filter-group">
            <label htmlFor="dateFilter">Date Filter:</label>
            <select 
              id="dateFilter" 
              name="dateFilter" 
              value={filters.dateFilter} 
              onChange={handleFilterChange}
            >
              <option value="">No Date Filter</option>
              <option value="dueDate">Due On</option>
              <option value="dueBefore">Due Before</option>
              <option value="dueAfter">Due After</option>
            </select>
          </div>
          
          {filters.dateFilter && (
            <div className="filter-group">
              <label htmlFor="dateValue">Date:</label>
              <input 
                type="date" 
                id="dateValue" 
                name="dateValue" 
                value={filters.dateValue} 
                onChange={handleFilterChange}
              />
            </div>
          )}
        </div>
        
        <div className="filter-buttons">
          <button className="apply-button" onClick={applyFilters}>Apply Filters</button>
          <button className="clear-button" onClick={clearFilters}>Clear Filters</button>
        </div>
      </div>
      
      <div className="tasks-content">
        {tasks.length === 0 ? (
          <p className="no-tasks">No tasks found.</p>
        ) : (
          <TaskList 
            tasks={tasks} 
            onDeleteTask={onDeleteTask}
            onUpdateTask={onUpdateTask}
            onFetchTask={onFetchTask}
            onUpdateTaskCompletion={onUpdateTaskCompletion}
            onUpdateTaskPriority={onUpdateTaskPriority}
            onUpdateTaskDueDate={onUpdateTaskDueDate}
          />
        )}
      </div>
    </div>
  );
};

export default TasksShownPage;;
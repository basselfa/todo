import React, { useState } from 'react';

const TaskItem = ({ 
  task, 
  onDeleteTask, 
  onUpdateTask,
  onFetchTask,
  onUpdateTaskCompletion,
  onUpdateTaskPriority,
  onUpdateTaskDueDate
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editedTitle, setEditedTitle] = useState(task.title || task.task);
  const [editedDescription, setEditedDescription] = useState(task.description);
  const [editedPriority, setEditedPriority] = useState(task.priority);
  const [editedDueDate, setEditedDueDate] = useState(task.dueDate || task.finishingDate);
  const [animate, setAnimate] = useState(false);

  // Handle complete update using the specialized endpoint
  const handleCompletionChange = async () => {
    // If task is already completed, call incomplete endpoint, otherwise call complete endpoint
    const newCompletionState = !task.completed;
    const result = await onUpdateTaskCompletion(task.id, newCompletionState);
    if (result) {
      if (newCompletionState) {
        // If task is being completed, show animation
        setAnimate(true);
        setTimeout(() => {
          setAnimate(false);
        }, 1000);
      }
    }
  };

  // Handle priority update using the specialized endpoint
  const handlePriorityChange = async (newPriority) => {
    setEditedPriority(newPriority);
    if (!isEditing) {
      await onUpdateTaskPriority(task.id, newPriority);
    }
  };

  // Handle due date update using the specialized endpoint
  // eslint-disable-next-line no-unused-vars
  const handleDueDateChange = async (newDate) => {
    setEditedDueDate(newDate);
    if (!isEditing) {
      await onUpdateTaskDueDate(task.id, newDate);
    }
  };

  // Handle full update
  const handleUpdate = async () => {
    const updatedTask = {
      ...task,
      title: editedTitle,
      description: editedDescription,
      priority: editedPriority,
      dueDate: editedDueDate
    };
    
    await onUpdateTask(task.id, updatedTask);
    setIsEditing(false);
  };

  const getPriorityColor = (priority) => {
    switch(priority.toUpperCase()) {
      case 'HIGH': return '#f44336';
      case 'MEDIUM': return '#ff9800';
      case 'LOW': return '#4caf50';
      default: return '#757575';
    }
  };

  const getDateColor = () => {
    if (!task.dueDate && !task.finishingDate) return '#757575';
    
    const today = new Date();
    const taskDate = new Date(task.dueDate || task.finishingDate);
    
    if (taskDate < today) {
      return '#f44336'; // Red for overdue tasks
    } else if (taskDate.toDateString() === today.toDateString()) {
      return '#ff9800'; // Orange for today's tasks
    }
    return '#4caf50'; // Green for future tasks
  };

  return (
    <div className={`task-item ${animate ? 'task-completed-animation' : ''}`}>
      {isEditing ? (
        <div className="task-edit-form">
          <input
            type="text"
            value={editedTitle}
            onChange={(e) => setEditedTitle(e.target.value)}
            className="edit-input"
            placeholder="Task title"
          />
          <textarea
            value={editedDescription}
            onChange={(e) => setEditedDescription(e.target.value)}
            className="edit-input"
            placeholder="Description"
          />
          <select
            value={editedPriority}
            onChange={(e) => handlePriorityChange(e.target.value)}
            className="edit-input"
          >
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
          </select>
          <input
            type="date"
            value={editedDueDate}
            onChange={(e) => setEditedDueDate(e.target.value)}
            className="date-input"
          />
          <div className="task-actions">
            <button className="button" onClick={handleUpdate}>Save</button>
            <button className="button" onClick={() => setIsEditing(false)}>Cancel</button>
          </div>
        </div>
      ) : (
        <div className="task-view">
          <div className="task-header">
            <input
              type="checkbox"
              checked={task.completed}
              onChange={handleCompletionChange}
            />
            <div className="task-content">
              <span className="task-title" style={{ 
                color: task.completed ? '#757575' : '#212121',
                textDecoration: task.completed ? 'line-through' : 'none'
              }}>
                {task.title || task.task}
              </span>
              {task.description && (
                <p className="task-description">{task.description}</p>
              )}
            </div>
          </div>
          <div className="task-footer">
            <div className="task-meta">
              <span className="priority-badge" style={{ backgroundColor: getPriorityColor(task.priority) }}>
                {task.priority}
              </span>
              <span className="task-date" style={{ color: getDateColor() }}>
                {task.dueDate || task.finishingDate || 'No due date'}
              </span>
            </div>
            <div className="task-actions">
              <button className="button edit-button" onClick={() => setIsEditing(true)}>
                Edit
              </button>
              <button className="button delete-button" onClick={() => onDeleteTask(task.id)}>
                Delete
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default TaskItem;
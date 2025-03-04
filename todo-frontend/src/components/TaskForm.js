import React, { useState } from 'react';

const TaskForm = ({ onAddTask }) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState('MEDIUM');
  const [dueDate, setDueDate] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim() || !dueDate) {
      alert('Please fill in all required fields');
      return;
    }
    
    try {
      const newTask = {
        title: title.trim(),
        description: description.trim(),
        priority,
        dueDate,
        completed: false
      };
      
      await onAddTask(newTask);
      
      // Reset form
      setTitle('');
      setDescription('');
      setPriority('MEDIUM');
      setDueDate('');
    } catch (error) {
      console.error('Error adding task:', error);
      alert('Error adding task');
    }
  };

  return (
    <form onSubmit={handleSubmit} className="task-form container">
      <div className="form-group">
        <input
          type="text"
          placeholder="Enter new task"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="form-input"
          required
        />
        <textarea
          placeholder="Description (optional)"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          className="form-input"
        />
        <select
          value={priority}
          onChange={(e) => setPriority(e.target.value)}
          className="form-input"
        >
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
        </select>
        <label htmlFor="due-date">Due date</label>
        <input
          id="due-date"
          type="date"
          value={dueDate}
          onChange={(e) => setDueDate(e.target.value)}
          className="form-input"
          required
        />
        <button type="submit" className="button">
          Add Task
        </button>
      </div>
    </form>
  );
};

export default TaskForm;
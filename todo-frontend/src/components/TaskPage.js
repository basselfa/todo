import React from 'react';
import TaskForm from './TaskForm';
const TaskPage = ({ onAddTask, tasksLoaded }) => {
  return (
    <div>
      <h1>Tasks</h1>
      <TaskForm onAddTask={onAddTask} />
      {!tasksLoaded && (
        <p>Click 'Fetch Tasks' to load tasks</p>
      )}
    </div>
  );
};
export default TaskPage;
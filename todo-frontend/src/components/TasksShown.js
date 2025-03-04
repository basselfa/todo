
import React from 'react';

const TasksShown = ({ tasks }) => {
  return (
    <div>
      <h2>Tasks Shown</h2>
      {tasks.length === 0 ? (
<p>No tasks found.</p>
      ) : (
        <ul>
          {tasks.map(task => (
            <li key={task.id}>{task.name}</li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default TasksShown;
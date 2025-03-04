import React from 'react';
import TaskItem from './TaskItem';
const TaskList = ({ 
  tasks, 
  onDeleteTask, 
  onUpdateTask,
  onFetchTask,
  onUpdateTaskCompletion,
  onUpdateTaskPriority,
  onUpdateTaskDueDate 
}) => {
  return (
    <div className="task-list">
      {tasks.map(task => (
        <TaskItem 
          key={task.id} 
          task={task} 
          onDeleteTask={onDeleteTask}
          onUpdateTask={onUpdateTask}
          onFetchTask={onFetchTask}
          onUpdateTaskCompletion={onUpdateTaskCompletion}
          onUpdateTaskPriority={onUpdateTaskPriority}
          onUpdateTaskDueDate={onUpdateTaskDueDate}
        />
      ))}
    </div>
  );
};
export default TaskList;;
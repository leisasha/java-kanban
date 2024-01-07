package tasktracker.manager;

import tasktracker.models.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int count = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();

    //public methods
    public void refreshEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isWholeNew = true;
            boolean isWholeDone = true;

            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = (Subtask) getTask(subtaskId);
                if (subtask.getStatus() == Status.NEW) {
                    isWholeDone = false;
                } else if (subtask.getStatus() == Status.DONE) {
                    isWholeNew = false;
                } else if (subtask.getStatus() == Status.IN_PROGRESS) {
                    isWholeNew = false;
                    isWholeDone = false;
                }
            }

            if (isWholeNew) {
                epic.setStatus(Status.NEW);
            } else if (isWholeDone) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    public void refreshStatus(Task task) {
        if (task.getClass() == Epic.class) {
            refreshEpicStatus((Epic) task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) getTask(subtask.getEpicId());
            refreshEpicStatus(epic);
        }
    }

    public void makeTask(Task task) {
        if (task.getId() == 0) {
            task.setId(++count);

            if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) getTask(subtask.getEpicId());
                epic.getSubtasksId().add(subtask.getId());
            }
        }
        updateTask(task);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        refreshStatus(task);
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void removeTask(int id) {
        Task task = getTask(id);
        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            for (int subtaskId : epic.getSubtasksId()) {
                tasks.remove(subtaskId);
            }
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) getTask(subtask.getEpicId());
            epic.getSubtasksId().remove((Integer) id);
            refreshEpicStatus(epic);
        }
        tasks.remove(id);
    }

    public void removeTasks() {
        tasks.clear();
        count = 0;
    }

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Subtask> getWholeSubtasks(Epic epic) {
        ArrayList<Subtask> resultFunction = new ArrayList<>();

        for (int subtaskId : epic.getSubtasksId()) {
            resultFunction.add((Subtask) getTask(subtaskId));
        }

        return resultFunction;
    }

    //get and set
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void setTasks(HashMap<Integer, Task> tasks) {
        this.tasks = tasks;
    }
}

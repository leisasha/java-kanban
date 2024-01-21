package manager;

import models.*;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {
    private int count = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //public methods
    @Override
    public void makeTask(Task task) {
        if (task.getId() == 0) {
            task.setId(++count);

            if (task.getClass() == Subtask.class) {
                Subtask subtask = (Subtask) task;
                Epic epic = (Epic) tasks.get(subtask.getEpicId());
                if (epic != null)
                    epic.getSubtasksId().add(subtask.getId());
                else
                    subtask.setEpicId(0);
            }
        }
        updateTask(task);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
        refreshStatus(task);
    }

    @Override
    public Task getTask(int id) {
        Task resultFunction = tasks.get(id);

        historyManager.add(resultFunction);

        return resultFunction;
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.get(id);
        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            for (int subtaskId : epic.getSubtasksId()) {
                tasks.remove(subtaskId);
            }
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            epic.getSubtasksId().remove((Integer) id);
            refreshEpicStatus(epic);
        }
        tasks.remove(id);
    }

    @Override
    public void removeTasks() {
        tasks.clear();
        count = 0;
    }

    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getWholeSubtasks(Epic epic) {
        ArrayList<Subtask> resultFunction = new ArrayList<>();

        for (int subtaskId : epic.getSubtasksId()) {
            resultFunction.add((Subtask) tasks.get(subtaskId));
        }

        return resultFunction;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    //private methods
    private void refreshEpicStatus(Epic epic) {
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            boolean isWholeNew = true;
            boolean isWholeDone = true;

            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = (Subtask) tasks.get(subtaskId);
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

    private void refreshStatus(Task task) {
        if (task.getClass() == Epic.class) {
            refreshEpicStatus((Epic) task);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            if (epic != null)
                refreshEpicStatus(epic);
        }
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

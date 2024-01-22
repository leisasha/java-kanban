package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.List;

public interface TaskManager {
    void makeTask(Task task);

    void updateTask(Task task);

    Task getTask(int id);

    void removeTask(int id);

    void removeTasks();

    List<Task> getTasksList();

    List<Subtask> getWholeSubtasks(Epic epic);

    List<Task> getHistory();
}

package manager;

import models.Epic;
import models.Subtask;
import models.Task;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    void makeTask(Task task);

    void updateTask(Task task);

    Task getTask(int id);

    void removeTask(int id);

    void removeTasks();

    ArrayList<Task> getTasksList();

    ArrayList<Subtask> getWholeSubtasks(Epic epic);

    ArrayList<Task> getHistory();
}
